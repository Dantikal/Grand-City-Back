package com.grandcity.backend.dto.crm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

/** A request as the CRM sees it: the enquiry plus pipeline and workload fields. */
@Data
public class LeadDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String kind;
    private String message;
    private String status;
    private String department;
    private String assigneeId;
    private String assigneeName;
    private String propertyId;
    private int openTasks;
    private int overdueTasks;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime nextDueAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime updatedAt;
}
