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
public class RequestDto {

    private Long id;

    @NotBlank(message = "Please enter your name")
    @Size(min = 2, max = 80)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @Pattern(regexp = "^[+()\\d\\s-]*$", message = "Enter a valid phone number")
    private String phone;

    @Pattern(regexp = "general|viewing|valuation|letting", message = "Invalid kind")
    private String kind = "general";

    @NotBlank
    @Size(min = 10, max = 2000, message = "Tell us a little more (10+ characters)")
    private String message;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;
}
