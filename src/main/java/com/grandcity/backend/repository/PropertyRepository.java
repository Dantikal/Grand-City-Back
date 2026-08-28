package com.grandcity.backend.repository;

import com.grandcity.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, String>, JpaSpecificationExecutor<Property> {

    Optional<Property> findByIdOrSlug(String id, String slug);

    boolean existsBySlug(String slug);
}
