package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "issuer", length = 200, nullable = false)
    private String issuer;

    @Column(name = "year", length = 20, nullable = false)
    private String year;

    @Column(name = "image", columnDefinition = "text", nullable = false)
    private String image;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
