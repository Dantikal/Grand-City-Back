package com.grandcity.backend.repository;

import com.grandcity.backend.entity.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<ContactRequest, Long> {

    /** Open leads per employee — used to balance auto-assignment. */
    long countByAssigneeIdAndStatusNotIn(String assigneeId, Collection<String> statuses);
}
