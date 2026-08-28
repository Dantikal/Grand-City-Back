package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.dto.AgentDto;
import com.grandcity.backend.entity.Agent;
import com.grandcity.backend.mapper.AgentMapper;
import com.grandcity.backend.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private final AgentRepository repository;
    private final AgentMapper mapper;

    public AgentService(AgentRepository repository, AgentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<AgentDto> list() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgentDto getByIdOrSlug(String idOrSlug) {
        Agent agent = repository.findByIdOrSlug(idOrSlug, idOrSlug)
                .orElseThrow(() -> new NotFoundException("Agent not found: " + idOrSlug));
        return mapper.toDto(agent);
    }

    @Transactional
    public AgentDto create(AgentDto dto) {
        Agent entity = new Agent();

        String id = (dto.getId() != null && !dto.getId().isBlank())
                ? dto.getId()
                : "agent-" + UUID.randomUUID().toString().substring(0, 8);
        entity.setId(id);

        String slug = (dto.getSlug() != null && !dto.getSlug().isBlank())
                ? slugify(dto.getSlug())
                : slugify(dto.getName());
        entity.setSlug(ensureUniqueSlug(slug, null));

        mapper.applyToEntity(dto, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public AgentDto update(String id, AgentDto dto) {
        Agent entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agent not found: " + id));

        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            String slug = slugify(dto.getSlug());
            if (!slug.equals(entity.getSlug())) {
                entity.setSlug(ensureUniqueSlug(slug, id));
            }
        }

        mapper.applyToEntity(dto, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Agent not found: " + id);
        }
        repository.deleteById(id);
    }

    private String ensureUniqueSlug(String base, String excludeId) {
        String candidate = base;
        int suffix = 2;
        while (true) {
            var existing = repository.findByIdOrSlug(candidate, candidate);
            boolean clashes = existing.isPresent() && !existing.get().getId().equals(excludeId);
            if (!clashes) {
                return candidate;
            }
            candidate = base + "-" + suffix;
            suffix++;
        }
    }

    private String slugify(String input) {
        String normalized = input.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return normalized.isBlank() ? "agent" : normalized;
    }
}
