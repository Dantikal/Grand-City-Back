package com.grandcity.backend.dto.crm;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

/** Partial update: only non-null fields are applied. */
@Data
public class TaskUpdateDto {

    @Size(min = 1, max = 200)
    private String title;

    private OffsetDateTime dueAt;

    private Boolean done;
}
