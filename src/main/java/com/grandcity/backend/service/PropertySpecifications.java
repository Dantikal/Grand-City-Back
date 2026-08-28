package com.grandcity.backend.service;

import com.grandcity.backend.entity.Property;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class PropertySpecifications {

    private PropertySpecifications() {
    }

    public static Specification<Property> filter(
            String query,
            String category,
            String listingType,
            String kind,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer beds,
            Boolean featured) {

        return (root, criteriaQuery, cb) -> {
            Predicate predicate = cb.conjunction();

            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase() + "%";
                Predicate textMatch = cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("area")), like),
                        cb.like(cb.lower(root.get("city")), like),
                        cb.like(cb.lower(root.get("kind")), like)
                );
                predicate = cb.and(predicate, textMatch);
            }

            if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }

            if (listingType != null && !listingType.isBlank() && !"all".equalsIgnoreCase(listingType)) {
                predicate = cb.and(predicate, cb.equal(root.get("listingType"), listingType));
            }

            if (kind != null && !kind.isBlank() && !"all".equalsIgnoreCase(kind)) {
                predicate = cb.and(predicate, cb.equal(root.get("kind"), kind));
            }

            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (beds != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("beds"), beds));
            }

            if (featured != null) {
                predicate = cb.and(predicate, cb.equal(root.get("featured"), featured));
            }

            return predicate;
        };
    }
}
