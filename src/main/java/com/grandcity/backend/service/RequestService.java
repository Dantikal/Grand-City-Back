package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.crm.CrmService;
import com.grandcity.backend.dto.RequestDto;
import com.grandcity.backend.entity.ContactRequest;
import com.grandcity.backend.mapper.RequestMapper;
import com.grandcity.backend.repository.PropertyRepository;
import com.grandcity.backend.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private final RequestRepository repository;
    private final RequestMapper mapper;
    private final CrmService crm;
    private final PropertyRepository properties;

    public RequestService(RequestRepository repository, RequestMapper mapper, CrmService crm,
                          PropertyRepository properties) {
        this.repository = repository;
        this.mapper = mapper;
        this.crm = crm;
        this.properties = properties;
    }

    @Transactional
    public RequestDto create(RequestDto dto) {
        String kind = dto.getKind() != null && !dto.getKind().isBlank() ? dto.getKind() : "general";
        String propertyId = dto.getPropertyId() != null && properties.existsById(dto.getPropertyId())
                ? dto.getPropertyId() : null;
        return mapper.toDto(createLead(dto.getName(), dto.getEmail(), dto.getPhone(), kind,
                dto.getMessage(), propertyId, dto.getAgentId()));
    }

    /** Save an enquiry and hand it to the CRM for routing, assignment and notification. */
    @Transactional
    public ContactRequest createLead(String name, String email, String phone, String kind,
                                     String message, String propertyId, String preferredAgentId) {
        OffsetDateTime now = OffsetDateTime.now();
        ContactRequest entity = repository.save(ContactRequest.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .kind(kind)
                .message(message)
                .propertyId(propertyId)
                .status("new")
                .createdAt(now)
                .updatedAt(now)
                .build());
        crm.onNewLead(entity, preferredAgentId);
        return entity;
    }

    @Transactional(readOnly = true)
    public List<RequestDto> list() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(ContactRequest::getCreatedAt).reversed())
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto updateStatus(Long id, String status) {
        ContactRequest entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found: " + id));
        entity.setStatus(status);
        entity.setUpdatedAt(OffsetDateTime.now());
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Request not found: " + id);
        }
        repository.deleteById(id);
    }
}
