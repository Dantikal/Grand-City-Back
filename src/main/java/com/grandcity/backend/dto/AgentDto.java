package com.grandcity.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDto {

    private String id;
    private String slug;

    @NotBlank
    private String name;

    @NotBlank
    private String role;

    private String bio;
    private String longBio;
    private String photo;

    @NotBlank
    @Email
    private String email;

    private String phone;

    private List<String> specialties;
    private List<String> areas;

    @PositiveOrZero
    private Integer salesCount;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private BigDecimal rating;

    @NotNull
    private Integer since;

    @Pattern(regexp = "valuation|planning|sales", message = "Invalid department")
    private String department;
}
