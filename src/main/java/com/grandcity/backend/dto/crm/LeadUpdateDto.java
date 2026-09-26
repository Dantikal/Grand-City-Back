package com.grandcity.backend.dto.crm;

import com.grandcity.backend.dto.CrmStages;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/** Partial update: only non-null fields are applied. An empty `assigneeId` unassigns. */
@Data
public class LeadUpdateDto {

    @Pattern(regexp = CrmStages.PATTERN, message = "Invalid status")
    private String status;

    private String assigneeId;

    @Pattern(regexp = "valuation|planning|sales", message = "Invalid department")
    private String department;
}
