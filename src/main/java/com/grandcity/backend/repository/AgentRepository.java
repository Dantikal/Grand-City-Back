package com.grandcity.backend.repository;

import com.grandcity.backend.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, String> {

    Optional<Agent> findByIdOrSlug(String id, String slug);

    boolean existsBySlug(String slug);
}
