package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.NotFoundException;
import com.grandcity.backend.dto.PropertyDto;
import com.grandcity.backend.entity.Property;
import com.grandcity.backend.mapper.PropertyMapper;
import com.grandcity.backend.repository.PropertyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository repository;
    private final PropertyMapper mapper;

    public PropertyService(PropertyRepository repository, PropertyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> list(String query, String category, String listingType, String kind,
                                   BigDecimal minPrice, BigDecimal maxPrice, Integer beds,
                                   String sort, Boolean featured, Integer limit) {

        Specification<Property> spec = PropertySpecifications.filter(
                query, category, listingType, kind, minPrice, maxPrice, beds, featured);

        Sort sortOrder = resolveSort(sort);

        List<Property> results;
        if (limit != null && limit > 0) {
            results = repository.findAll(spec, PageRequest.of(0, limit, sortOrder)).getContent();
        } else {
            results = repository.findAll(spec, sortOrder);
        }

        return results.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    private Sort resolveSort(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "beds-desc" -> Sort.by(Sort.Direction.DESC, "beds");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    @Transactional(readOnly = true)
    public PropertyDto getByIdOrSlug(String idOrSlug) {
        Property property = repository.findByIdOrSlug(idOrSlug, idOrSlug)
                .orElseThrow(() -> new NotFoundException("Property not found: " + idOrSlug));
        return mapper.toDto(property);
    }

    @Transactional
    public PropertyDto create(PropertyDto dto) {
        Property entity = new Property();

        String id = (dto.getId() != null && !dto.getId().isBlank())
                ? dto.getId()
                : "prop-" + UUID.randomUUID().toString().substring(0, 8);
        entity.setId(id);

        String slug = (dto.getSlug() != null && !dto.getSlug().isBlank())
                ? slugify(dto.getSlug())
                : slugify(dto.getTitle());
        entity.setSlug(ensureUniqueSlug(slug, null));

        mapper.applyToEntity(dto, entity);
        entity.setCreatedAt(OffsetDateTime.now());

        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public PropertyDto update(String id, PropertyDto dto) {
        Property entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found: " + id));

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
            throw new NotFoundException("Property not found: " + id);
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
        return normalized.isBlank() ? "listing" : normalized;
    }
}
