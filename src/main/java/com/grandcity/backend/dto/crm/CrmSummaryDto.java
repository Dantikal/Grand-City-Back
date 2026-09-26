package com.grandcity.backend.dto.crm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CrmSummaryDto {

    /** Lead count per pipeline stage. */
    private Map<String, Long> byStage;
    private long unassigned;
    private long openTasks;
    private long overdueTasks;
    private boolean telegramEnabled;
}
