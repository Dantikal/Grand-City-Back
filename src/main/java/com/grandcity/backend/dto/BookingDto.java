package com.grandcity.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotBlank
    private String propertyId;

    @NotBlank(message = "Please enter your name")
    @Size(min = 2, max = 80)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Enter a valid phone number")
    @Pattern(regexp = "^[+()\\d\\s-]+$", message = "Enter a valid phone number")
    private String phone;

    @NotBlank(message = "Pick a date")
    private String date;

    @Size(max = 1000)
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;
}
