package com.grandcity.backend.repository;

import com.grandcity.backend.entity.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<ContactRequest, Long> {
}
