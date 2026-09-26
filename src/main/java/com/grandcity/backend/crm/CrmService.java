package com.grandcity.backend.crm;

import com.grandcity.backend.common.exception.BadRequestException;
import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.dto.CrmStages;
import com.grandcity.backend.dto.crm.*;
import com.grandcity.backend.entity.Agent;
import com.grandcity.backend.entity.ContactRequest;
import com.grandcity.backend.entity.CrmNote;
import com.grandcity.backend.entity.CrmTask;
import com.grandcity.backend.repository.AgentRepository;
import com.grandcity.backend.repository.CrmNoteRepository;
import com.grandcity.backend.repository.CrmTaskRepository;
import com.grandcity.backend.repository.PropertyRepository;
import com.grandcity.backend.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lead pipeline: routing new enquiries to a department, balancing them across
 * that department's employees, and keeping a timeline of notes, stage changes and tasks.
 */
@Service
public class CrmService {

    /** Which department handles each enquiry type; unlisted kinds go to the least busy employee overall. */
    private static final Map<String, String> KIND_DEPARTMENT = Map.of(
            "valuation", "valuation",
            "business-plan", "planning",
            "viewing", "sales",
            "sales", "sales",
            "letting", "sales");

    static final Map<String, String> STAGE_LABELS = Map.of(
            "new", "Новая",
            "in-progress", "В работе",
            "meeting", "Встреча / показ",
            "contract", "Договор",
            "won", "Успешно",
            "lost", "Отказ");

    static final Map<String, String> DEPARTMENT_LABELS = Map.of(
            "valuation", "Отдел оценки",
            "planning", "Отдел бизнес-планирования",
            "sales", "Отдел купли-продажи");

    private static final Map<String, String> KIND_LABELS = Map.of(
            "general", "Общий вопрос",
            "viewing", "Запись на просмотр",
            "valuation", "Оценка",
            "business-plan", "Бизнес-план",
            "sales", "Купля-продажа",
            "letting", "Аренда");

    private final RequestRepository requests;
    private final AgentRepository agents;
    private final PropertyRepository properties;
    private final CrmNoteRepository notes;
    private final CrmTaskRepository tasks;
    private final TelegramNotifier telegram;
    private final String adminUrl;
    private final ZoneId zone;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public CrmService(RequestRepository requests,
                      AgentRepository agents,
                      PropertyRepository properties,
                      CrmNoteRepository notes,
                      CrmTaskRepository tasks,
                      TelegramNotifier telegram,
                      @Value("${app.crm.admin-url}") String adminUrl,
                      @Value("${app.crm.timezone}") String timezone) {
        this.requests = requests;
        this.agents = agents;
        this.properties = properties;
        this.notes = notes;
        this.tasks = tasks;
        this.telegram = telegram;
        this.adminUrl = adminUrl;
        this.zone = ZoneId.of(timezone);
    }

    // ---------------------------------------------------------------- automation

    /**
     * Route, assign and announce a freshly saved enquiry. When the client wrote to a specific
     * employee (the "message this agent" form), that employee gets the lead.
     */
    @Transactional
    public void onNewLead(ContactRequest lead, String preferredAgentId) {
        Optional<Agent> preferred = preferredAgentId == null ? Optional.empty() : agents.findById(preferredAgentId);
        String department = preferred.map(Agent::getDepartment).orElse(KIND_DEPARTMENT.get(lead.getKind()));
        Optional<Agent> assignee = preferred.isPresent() ? preferred : pickAssignee(department);

        lead.setDepartment(department != null ? department : assignee.map(Agent::getDepartment).orElse(null));
        lead.setAssigneeId(assignee.map(Agent::getId).orElse(null));
        requests.save(lead);

        String source = "viewing".equals(lead.getKind()) ? "запись на просмотр с сайта" : "форма на сайте";
        system(lead.getId(), "Заявка получена: " + source + ".");
        if (lead.getPropertyId() != null) {
            properties.findById(lead.getPropertyId()).ifPresent(p -> system(lead.getId(), "Объект: " + p.getTitle() + "."));
        }
        system(lead.getId(), preferred.isPresent()
                ? "Клиент написал сотруднику напрямую — назначено: " + preferred.get().getName() + "."
                : assignee
                .map(a -> department != null && !department.equals(a.getDepartment())
                        ? "В отделе " + DEPARTMENT_LABELS.get(department).substring("Отдел ".length()) + " нет сотрудников — назначено самому свободному: " + a.getName() + "."
                        : "Автоматически назначено: " + a.getName() + deptSuffix(lead.getDepartment()) + ".")
                .orElse("Не удалось назначить: нет сотрудников. Назначьте вручную."));

        telegram.send(newLeadMessage(lead, assignee.orElse(null)));
    }

    /**
     * The employee with the fewest open leads — within the department if it has anyone,
     * otherwise across all employees. Ties go to the alphabetically first name.
     */
    Optional<Agent> pickAssignee(String department) {
        List<Agent> all = agents.findAll();
        List<Agent> pool = department == null ? all
                : all.stream().filter(a -> department.equals(a.getDepartment())).toList();
        if (pool.isEmpty()) pool = all;
        return pool.stream().min(Comparator
                .comparingLong((Agent a) -> requests.countByAssigneeIdAndStatusNotIn(a.getId(), CrmStages.CLOSED))
                .thenComparing(Agent::getName));
    }

    // ---------------------------------------------------------------- leads

    @Transactional(readOnly = true)
    public List<LeadDto> listLeads() {
        Map<String, Agent> agentById = agentsById();
        Map<Long, List<CrmTask>> openByLead = tasks.findByDoneFalseOrderByDueAtAsc().stream()
                .collect(Collectors.groupingBy(CrmTask::getRequestId));
        return requests.findAll().stream()
                .sorted(Comparator.comparing(ContactRequest::getCreatedAt).reversed())
                .map(r -> toLead(r, agentById, openByLead.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public LeadDetailDto getLead(Long id) {
        ContactRequest r = find(id);
        Map<String, Agent> agentById = agentsById();
        List<CrmTask> leadTasks = tasks.findByRequestIdOrderByDueAtAsc(id);
        LeadDto lead = toLead(r, agentById, leadTasks.stream().filter(t -> !t.isDone()).toList());
        return new LeadDetailDto(
                lead,
                notes.findByRequestIdOrderByCreatedAtDesc(id).stream().map(this::toNote).toList(),
                leadTasks.stream().map(t -> toTask(t, r, agentById)).toList());
    }

    @Transactional
    public LeadDto updateLead(Long id, LeadUpdateDto dto) {
        ContactRequest r = find(id);
        Map<String, Agent> agentById = agentsById();

        if (dto.getStatus() != null && !dto.getStatus().equals(r.getStatus())) {
            system(id, "Этап: " + stage(r.getStatus()) + " → " + stage(dto.getStatus()) + ".");
            r.setStatus(dto.getStatus());
            if (CrmStages.CLOSED.contains(dto.getStatus())) {
                telegram.send((dto.getStatus().equals("won") ? "✅ Сделка закрыта успешно" : "❌ Отказ")
                        + ": <b>" + TelegramNotifier.esc(r.getName()) + "</b>\n" + link(id));
            }
        }
        if (dto.getDepartment() != null && !dto.getDepartment().equals(r.getDepartment())) {
            system(id, "Отдел: " + DEPARTMENT_LABELS.get(dto.getDepartment()) + ".");
            r.setDepartment(dto.getDepartment());
        }
        if (dto.getAssigneeId() != null) {
            String next = dto.getAssigneeId().isBlank() ? null : dto.getAssigneeId();
            if (!Objects.equals(next, r.getAssigneeId())) {
                if (next != null && !agentById.containsKey(next)) {
                    throw new BadRequestException("Unknown employee: " + next);
                }
                r.setAssigneeId(next);
                system(id, next == null ? "Ответственный снят." : "Ответственный: " + agentById.get(next).getName() + ".");
                if (next != null) {
                    telegram.send("👤 <b>" + TelegramNotifier.esc(agentById.get(next).getName())
                            + "</b> назначен(а) на заявку <b>" + TelegramNotifier.esc(r.getName()) + "</b>\n" + link(id));
                }
            }
        }
        r.setUpdatedAt(OffsetDateTime.now());
        requests.save(r);
        List<CrmTask> open = tasks.findByRequestIdOrderByDueAtAsc(id).stream().filter(t -> !t.isDone()).toList();
        return toLead(r, agentById, open);
    }

    @Transactional
    public NoteDto addNote(Long id, NoteCreateDto dto) {
        find(id);
        CrmNote note = notes.save(CrmNote.builder()
                .requestId(id)
                .kind("note")
                .text(dto.getText().trim())
                .author(currentUser())
                .createdAt(OffsetDateTime.now())
                .build());
        touch(id);
        return toNote(note);
    }

    // ---------------------------------------------------------------- tasks

    @Transactional
    public TaskDto addTask(Long leadId, TaskCreateDto dto) {
        ContactRequest r = find(leadId);
        Map<String, Agent> agentById = agentsById();
        String assignee = dto.getAssigneeId() != null && !dto.getAssigneeId().isBlank()
                ? dto.getAssigneeId() : r.getAssigneeId();
        if (assignee != null && !agentById.containsKey(assignee)) {
            throw new BadRequestException("Unknown employee: " + assignee);
        }
        CrmTask task = tasks.save(CrmTask.builder()
                .requestId(leadId)
                .title(dto.getTitle().trim())
                .dueAt(dto.getDueAt())
                .assigneeId(assignee)
                .createdAt(OffsetDateTime.now())
                .build());
        system(leadId, "Задача: «" + task.getTitle() + "» до " + format(task.getDueAt()) + ".");
        touch(leadId);
        return toTask(task, r, agentById);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> listOpenTasks() {
        Map<String, Agent> agentById = agentsById();
        Map<Long, ContactRequest> leads = requests.findAll().stream()
                .collect(Collectors.toMap(ContactRequest::getId, Function.identity()));
        return tasks.findByDoneFalseOrderByDueAtAsc().stream()
                .map(t -> toTask(t, leads.get(t.getRequestId()), agentById))
                .toList();
    }

    @Transactional
    public TaskDto updateTask(Long taskId, TaskUpdateDto dto) {
        CrmTask task = tasks.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found: " + taskId));
        if (dto.getTitle() != null) task.setTitle(dto.getTitle().trim());
        if (dto.getDueAt() != null && !dto.getDueAt().equals(task.getDueAt())) {
            task.setDueAt(dto.getDueAt());
            task.setReminded(false); // a new deadline deserves a new reminder
        }
        if (dto.getDone() != null && dto.getDone() != task.isDone()) {
            task.setDone(dto.getDone());
            system(task.getRequestId(), (dto.getDone() ? "Задача выполнена: «" : "Задача снова открыта: «")
                    + task.getTitle() + "».");
        }
        tasks.save(task);
        touch(task.getRequestId());
        return toTask(task, find(task.getRequestId()), agentsById());
    }

    @Transactional
    public void deleteTask(Long taskId) {
        CrmTask task = tasks.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found: " + taskId));
        tasks.delete(task);
        system(task.getRequestId(), "Задача удалена: «" + task.getTitle() + "».");
    }

    /**
     * Called by the scheduler: announce tasks that are about to fall due. Tasks overdue by more
     * than a day are left out, so switching Telegram on later doesn't flood the chat with a
     * backlog — those are already flagged red in the CRM.
     */
    @Transactional
    public int sendDueReminders(int minutesBefore) {
        OffsetDateTime now = OffsetDateTime.now();
        List<CrmTask> due = tasks.findByDoneFalseAndRemindedFalseAndDueAtBetween(
                now.minusDays(1), now.plusMinutes(minutesBefore));
        if (due.isEmpty() || !telegram.isEnabled()) return 0;
        Map<String, Agent> agentById = agentsById();
        for (CrmTask t : due) {
            ContactRequest r = requests.findById(t.getRequestId()).orElse(null);
            Agent a = t.getAssigneeId() != null ? agentById.get(t.getAssigneeId()) : null;
            String overdue = t.getDueAt().isBefore(OffsetDateTime.now()) ? " (просрочена)" : "";
            telegram.send("⏰ <b>Напоминание" + overdue + "</b>\n"
                    + TelegramNotifier.esc(t.getTitle()) + "\n"
                    + "Срок: " + format(t.getDueAt()) + "\n"
                    + (r != null ? "Клиент: " + TelegramNotifier.esc(r.getName()) + "\n" : "")
                    + (a != null ? "Ответственный: " + TelegramNotifier.esc(a.getName()) + "\n" : "")
                    + link(t.getRequestId()));
            t.setReminded(true);
        }
        tasks.saveAll(due);
        return due.size();
    }

    // ---------------------------------------------------------------- summary

    @Transactional(readOnly = true)
    public CrmSummaryDto summary() {
        List<ContactRequest> all = requests.findAll();
        Map<String, Long> byStage = new LinkedHashMap<>();
        for (String s : CrmStages.PATTERN.split("\\|")) byStage.put(s, 0L);
        all.forEach(r -> byStage.merge(r.getStatus(), 1L, Long::sum));
        long unassigned = all.stream()
                .filter(r -> r.getAssigneeId() == null && !CrmStages.CLOSED.contains(r.getStatus()))
                .count();
        List<CrmTask> open = tasks.findByDoneFalseOrderByDueAtAsc();
        OffsetDateTime now = OffsetDateTime.now();
        long overdue = open.stream().filter(t -> t.getDueAt().isBefore(now)).count();
        return new CrmSummaryDto(byStage, unassigned, open.size(), overdue, telegram.isEnabled());
    }

    public boolean sendTestMessage() {
        return telegram.sendNow("✅ Grand City CRM подключена. Сюда будут приходить новые заявки и напоминания о задачах.");
    }

    // ---------------------------------------------------------------- helpers

    private String newLeadMessage(ContactRequest r, Agent assignee) {
        StringBuilder sb = new StringBuilder("🆕 <b>Новая заявка</b> — ")
                .append(TelegramNotifier.esc(KIND_LABELS.getOrDefault(r.getKind(), r.getKind()))).append("\n")
                .append("👤 ").append(TelegramNotifier.esc(r.getName())).append("\n");
        if (r.getPhone() != null && !r.getPhone().isBlank()) {
            sb.append("📞 ").append(TelegramNotifier.esc(r.getPhone())).append("\n");
        }
        sb.append("✉️ ").append(TelegramNotifier.esc(r.getEmail())).append("\n");
        if (r.getPropertyId() != null) {
            properties.findById(r.getPropertyId()).ifPresent(p ->
                    sb.append("🏠 ").append(TelegramNotifier.esc(p.getTitle())).append("\n"));
        }
        String msg = r.getMessage() == null ? "" : r.getMessage();
        if (msg.length() > 500) msg = msg.substring(0, 500) + "…";
        sb.append("\n").append(TelegramNotifier.esc(msg)).append("\n\n");
        sb.append(assignee != null
                ? "Ответственный: <b>" + TelegramNotifier.esc(assignee.getName()) + "</b>" + deptSuffix(r.getDepartment())
                : "⚠️ Ответственный не назначен");
        sb.append("\n").append(link(r.getId()));
        return sb.toString();
    }

    private LeadDto toLead(ContactRequest r, Map<String, Agent> agentById, List<CrmTask> openTasks) {
        OffsetDateTime now = OffsetDateTime.now();
        LeadDto d = new LeadDto();
        d.setId(r.getId());
        d.setName(r.getName());
        d.setEmail(r.getEmail());
        d.setPhone(r.getPhone());
        d.setKind(r.getKind());
        d.setMessage(r.getMessage());
        d.setStatus(r.getStatus());
        d.setDepartment(r.getDepartment());
        d.setAssigneeId(r.getAssigneeId());
        Agent a = r.getAssigneeId() != null ? agentById.get(r.getAssigneeId()) : null;
        d.setAssigneeName(a != null ? a.getName() : null);
        d.setPropertyId(r.getPropertyId());
        d.setOpenTasks(openTasks.size());
        d.setOverdueTasks((int) openTasks.stream().filter(t -> t.getDueAt().isBefore(now)).count());
        d.setNextDueAt(openTasks.stream().map(CrmTask::getDueAt).min(Comparator.naturalOrder()).orElse(null));
        d.setCreatedAt(r.getCreatedAt());
        d.setUpdatedAt(r.getUpdatedAt());
        return d;
    }

    private NoteDto toNote(CrmNote n) {
        NoteDto d = new NoteDto();
        d.setId(n.getId());
        d.setKind(n.getKind());
        d.setText(n.getText());
        d.setAuthor(n.getAuthor());
        d.setCreatedAt(n.getCreatedAt());
        return d;
    }

    private TaskDto toTask(CrmTask t, ContactRequest lead, Map<String, Agent> agentById) {
        TaskDto d = new TaskDto();
        d.setId(t.getId());
        d.setLeadId(t.getRequestId());
        d.setLeadName(lead != null ? lead.getName() : null);
        d.setTitle(t.getTitle());
        d.setDone(t.isDone());
        d.setDueAt(t.getDueAt());
        d.setAssigneeId(t.getAssigneeId());
        Agent a = t.getAssigneeId() != null ? agentById.get(t.getAssigneeId()) : null;
        d.setAssigneeName(a != null ? a.getName() : null);
        d.setCreatedAt(t.getCreatedAt());
        return d;
    }

    private ContactRequest find(Long id) {
        return requests.findById(id).orElseThrow(() -> new NotFoundException("Request not found: " + id));
    }

    private Map<String, Agent> agentsById() {
        return agents.findAll().stream().collect(Collectors.toMap(Agent::getId, Function.identity()));
    }

    private void system(Long requestId, String text) {
        notes.save(CrmNote.builder()
                .requestId(requestId)
                .kind("system")
                .text(text)
                .author(currentUser())
                .createdAt(OffsetDateTime.now())
                .build());
    }

    private void touch(Long requestId) {
        requests.findById(requestId).ifPresent(r -> {
            r.setUpdatedAt(OffsetDateTime.now());
            requests.save(r);
        });
    }

    private static String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()) ? auth.getName() : "";
    }

    private static String stage(String s) {
        return STAGE_LABELS.getOrDefault(s, s);
    }

    private static String deptSuffix(String department) {
        return department != null ? " (" + DEPARTMENT_LABELS.get(department) + ")" : "";
    }

    private String format(OffsetDateTime t) {
        return t.atZoneSameInstant(zone).format(dateFormat);
    }

    private String link(Long id) {
        String url = adminUrl + "?lead=" + id;
        // Telegram refuses HTML links to local hosts, so during development show the bare URL.
        return url.contains("://localhost") || url.contains("://127.0.0.1")
                ? url : "<a href=\"" + url + "\">Открыть в CRM</a>";
    }
}
