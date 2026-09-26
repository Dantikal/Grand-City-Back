package com.grandcity.backend.dto.crm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TaskDto {

    private Long id;
    private Long leadId;
    private String leadName;
    private String title;
    private boolean done;
    private String assigneeId;
    private String assigneeName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime dueAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;
}
