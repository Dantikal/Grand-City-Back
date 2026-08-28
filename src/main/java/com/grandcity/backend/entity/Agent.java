package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "role", nullable = false, length = 160)
    private String role;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "long_bio", columnDefinition = "text")
    private String longBio;

    @Column(name = "photo", columnDefinition = "text")
    private String photo;

    @Column(name = "email", nullable = false, length = 160)
    private String email;

    @Column(name = "phone", length = 60)
    private String phone;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "specialties", columnDefinition = "text[]")
    @Builder.Default
    private List<String> specialties = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "areas", columnDefinition = "text[]")
    @Builder.Default
    private List<String> areas = new ArrayList<>();

    @Column(name = "sales_count", nullable = false)
    @Builder.Default
    private Integer salesCount = 0;

    @Column(name = "rating", precision = 2, scale = 1, nullable = false)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "since", nullable = false)
    private Integer since;
}
