package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "slug", nullable = false, unique = true, length = 160)
    private String slug;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "area", nullable = false, length = 200)
    private String area;

    @Column(name = "city", nullable = false, length = 120)
    private String city;

    @Column(name = "price", nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    /** "month" for rentals, null otherwise. */
    @Column(name = "rent_period", length = 20)
    private String rentPeriod;

    /** "complex" | "home" */
    @Column(name = "category", nullable = false, length = 20)
    private String category;

    /** "buy" | "rent" | "luxury" */
    @Column(name = "listing_type", nullable = false, length = 20)
    private String listingType;

    /** house|bungalow|loft|apartment|townhouse|new-build */
    @Column(name = "kind", nullable = false, length = 30)
    private String kind;

    /** for-sale|to-let|new-build|sold */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "beds", nullable = false)
    private Integer beds;

    @Column(name = "baths", nullable = false)
    private Integer baths;

    @Column(name = "sqft", nullable = false)
    private Integer sqft;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "images", columnDefinition = "text[]")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "features", columnDefinition = "text[]")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    @Column(name = "agent_id", length = 64)
    private String agentId;

    @Column(name = "lat", precision = 10, scale = 6)
    private BigDecimal lat;

    @Column(name = "lng", precision = 10, scale = 6)
    private BigDecimal lng;

    @Column(name = "featured", nullable = false)
    @Builder.Default
    private boolean featured = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
