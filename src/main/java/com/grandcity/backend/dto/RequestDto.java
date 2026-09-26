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

    @Pattern(regexp = "general|viewing|valuation|business-plan|sales|letting", message = "Invalid kind")
    private String kind = "general";

    @NotBlank
    @Size(min = 10, max = 2000, message = "Tell us a little more (10+ characters)")
    private String message;

    private String status;

    /** Optional: the employee the client wrote to — they get the lead. */
    @Size(max = 64)
    private String agentId;

    /** Optional: the property the enquiry is about. */
    @Size(max = 64)
    private String propertyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;
}
