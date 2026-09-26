package com.grandcity.backend.dto.crm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeadDetailDto {

    private LeadDto lead;
    private List<NoteDto> notes;
    private List<TaskDto> tasks;
}
