package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.dto.RequestDto;
import com.grandcity.backend.entity.ContactRequest;
import com.grandcity.backend.mapper.RequestMapper;
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

    public RequestService(RequestRepository repository, RequestMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public RequestDto create(RequestDto dto) {
        ContactRequest entity = ContactRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .kind(dto.getKind() != null && !dto.getKind().isBlank() ? dto.getKind() : "general")
                .message(dto.getMessage())
                .status("new")
                .createdAt(OffsetDateTime.now())
                .build();
        return mapper.toDto(repository.save(entity));
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
