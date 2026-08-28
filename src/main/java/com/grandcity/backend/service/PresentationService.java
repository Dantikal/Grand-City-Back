package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.dto.PresentationDto;
import com.grandcity.backend.entity.Presentation;
import com.grandcity.backend.repository.PresentationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PresentationService {

    private final PresentationRepository repository;

    public PresentationService(PresentationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Map<String, PresentationDto> getAll() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(Presentation::getLang, p -> new PresentationDto(p.getUrl(), p.getName())));
    }

    @Transactional
    public PresentationDto save(String lang, PresentationDto dto) {
        Presentation entity = repository.findById(lang)
                .orElse(new Presentation(lang, "", ""));
        entity.setUrl(dto.getUrl() != null ? dto.getUrl() : "");
        entity.setName(dto.getName() != null ? dto.getName() : "");
        return toDto(repository.save(entity));
    }

    @Transactional
    public void delete(String lang) {
        if (!repository.existsById(lang)) {
            throw new NotFoundException("Presentation not found for language: " + lang);
        }
        repository.deleteById(lang);
    }

    private PresentationDto toDto(Presentation p) {
        return new PresentationDto(p.getUrl(), p.getName());
    }
}
