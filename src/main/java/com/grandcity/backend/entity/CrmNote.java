package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "crm_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrmNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    /** note | system */
    @Column(name = "kind", nullable = false, length = 10)
    @Builder.Default
    private String kind = "note";

    @Column(name = "text", nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "author", nullable = false, length = 80)
    @Builder.Default
    private String author = "";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
