package com.grandcity.backend.repository;

import com.grandcity.backend.entity.CrmTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface CrmTaskRepository extends JpaRepository<CrmTask, Long> {

    List<CrmTask> findByRequestIdOrderByDueAtAsc(Long requestId);

    List<CrmTask> findByDoneFalseOrderByDueAtAsc();

    /** Open, not-yet-reminded tasks falling due inside the window. */
    List<CrmTask> findByDoneFalseAndRemindedFalseAndDueAtBetween(OffsetDateTime from, OffsetDateTime to);
}
