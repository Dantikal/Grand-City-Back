package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.dto.CertificateDto;
import com.grandcity.backend.entity.Certificate;
import com.grandcity.backend.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CertificateRepository repository;

    public CertificateService(CertificateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CertificateDto> list() {
        return repository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificateDto create(CertificateDto dto) {
        String id = (dto.getId() != null && !dto.getId().isBlank())
                ? dto.getId()
                : "cert-" + UUID.randomUUID().toString().substring(0, 8);

        int maxOrder = repository.findAllByOrderBySortOrderAsc().stream()
                .mapToInt(Certificate::getSortOrder)
                .max()
                .orElse(-1);

        Certificate entity = Certificate.builder()
                .id(id)
                .title(dto.getTitle() != null ? dto.getTitle() : "")
                .issuer(dto.getIssuer() != null ? dto.getIssuer() : "")
                .year(dto.getYear() != null ? dto.getYear() : "")
                .image(dto.getImage() != null ? dto.getImage() : "")
                .sortOrder(maxOrder + 1)
                .build();

        return toDto(repository.save(entity));
    }

    @Transactional
    public CertificateDto update(String id, CertificateDto dto) {
        Certificate entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Certificate not found: " + id));

        entity.setTitle(dto.getTitle() != null ? dto.getTitle() : entity.getTitle());
        entity.setIssuer(dto.getIssuer() != null ? dto.getIssuer() : entity.getIssuer());
        entity.setYear(dto.getYear() != null ? dto.getYear() : entity.getYear());
        entity.setImage(dto.getImage() != null ? dto.getImage() : entity.getImage());

        return toDto(repository.save(entity));
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Certificate not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void reorder(List<String> ids) {
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            Certificate entity = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Certificate not found: " + id));
            entity.setSortOrder(i);
            repository.save(entity);
        }
    }

    private CertificateDto toDto(Certificate c) {
        return new CertificateDto(c.getId(), c.getTitle(), c.getIssuer(), c.getYear(), c.getImage());
    }
}
