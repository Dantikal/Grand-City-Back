package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "crm_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrmTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "due_at", nullable = false)
    private OffsetDateTime dueAt;

    @Column(name = "done", nullable = false)
    @Builder.Default
    private boolean done = false;

    /** The Telegram reminder has been sent. */
    @Column(name = "reminded", nullable = false)
    @Builder.Default
    private boolean reminded = false;

    @Column(name = "assignee_id", length = 64)
    private String assigneeId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
