package com.grandcity.backend.repository;

import com.grandcity.backend.entity.CrmNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrmNoteRepository extends JpaRepository<CrmNote, Long> {

    List<CrmNote> findByRequestIdOrderByCreatedAtDesc(Long requestId);
}
