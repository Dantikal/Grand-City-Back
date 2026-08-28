package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "email", nullable = false, length = 160)
    private String email;

    @Column(name = "phone", length = 60)
    private String phone;

    /** general | viewing | valuation | letting */
    @Column(name = "kind", nullable = false, length = 20)
    private String kind;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    /** new | in-progress | closed */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "new";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
