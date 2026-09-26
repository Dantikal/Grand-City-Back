package com.grandcity.backend.dto.crm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TaskCreateDto {

    @NotBlank(message = "Task title is required")
    @Size(max = 200)
    private String title;

    @NotNull(message = "Pick a due date")
    private OffsetDateTime dueAt;

    /** Defaults to the lead's assignee. */
    private String assigneeId;
}
