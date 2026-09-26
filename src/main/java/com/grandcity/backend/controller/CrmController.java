package com.grandcity.backend.controller;

import com.grandcity.backend.crm.CrmService;
import com.grandcity.backend.dto.crm.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Admin-only CRM API (every route requires a JWT, see SecurityConfig). */
@RestController
@RequestMapping("/crm")
public class CrmController {

    private final CrmService crm;

    public CrmController(CrmService crm) {
        this.crm = crm;
    }

    @GetMapping("/summary")
    public CrmSummaryDto summary() {
        return crm.summary();
    }

    @GetMapping("/leads")
    public List<LeadDto> leads() {
        return crm.listLeads();
    }

    @GetMapping("/leads/{id}")
    public LeadDetailDto lead(@PathVariable Long id) {
        return crm.getLead(id);
    }

    @PatchMapping("/leads/{id}")
    public LeadDto updateLead(@PathVariable Long id, @Valid @RequestBody LeadUpdateDto dto) {
        return crm.updateLead(id, dto);
    }

    @PostMapping("/leads/{id}/notes")
    public ResponseEntity<NoteDto> addNote(@PathVariable Long id, @Valid @RequestBody NoteCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(crm.addNote(id, dto));
    }

    @PostMapping("/leads/{id}/tasks")
    public ResponseEntity<TaskDto> addTask(@PathVariable Long id, @Valid @RequestBody TaskCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(crm.addTask(id, dto));
    }

    @GetMapping("/tasks")
    public List<TaskDto> openTasks() {
        return crm.listOpenTasks();
    }

    @PatchMapping("/tasks/{id}")
    public TaskDto updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto dto) {
        return crm.updateTask(id, dto);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        crm.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/telegram/test")
    public Map<String, Boolean> telegramTest() {
        return Map.of("sent", crm.sendTestMessage());
    }
}
