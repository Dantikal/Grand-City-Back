package com.grandcity.backend.dto.crm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteCreateDto {

    @NotBlank(message = "Note is empty")
    @Size(max = 4000)
    private String text;
}
