package com.grandcity.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDto {

    private String id;
    private String slug;

    @NotBlank(message = "Title is too short")
    @Size(min = 3, max = 120, message = "Title is too short")
    private String title;

    @NotBlank(message = "Add a neighborhood")
    @Size(min = 2)
    private String area;

    @NotBlank(message = "Add a city")
    @Size(min = 2)
    private String city;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    /** "month" for rentals, null otherwise */
    private String rentPeriod;

    @NotBlank
    @Pattern(regexp = "complex|home", message = "Invalid category")
    private String category;

    @NotBlank
    @Pattern(regexp = "buy|rent|luxury", message = "Invalid listingType")
    private String listingType;

    @NotBlank
    @Pattern(regexp = "house|bungalow|loft|apartment|townhouse|new-build", message = "Invalid kind")
    private String kind;

    @NotBlank
    @Pattern(regexp = "for-sale|to-let|new-build|sold", message = "Invalid status")
    private String status;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer beds;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer baths;

    @NotNull
    @Positive
    private Integer sqft;

    private List<String> images;

    @Size(min = 10, max = 4000, message = "Add a short description")
    private String description;

    private List<String> features;

    @NotBlank(message = "Assign an agent")
    private String agentId;

    @NotNull
    private CoordinatesDto coordinates;

    private boolean featured;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;
}
