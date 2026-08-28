package com.grandcity.backend.repository;

import com.grandcity.backend.entity.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationRepository extends JpaRepository<Presentation, String> {
}
