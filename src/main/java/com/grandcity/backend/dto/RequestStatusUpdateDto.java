package com.grandcity.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestStatusUpdateDto {

    @NotBlank
    @Pattern(regexp = "new|in-progress|closed", message = "Invalid status")
    private String status;
}
