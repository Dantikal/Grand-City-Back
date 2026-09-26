package com.grandcity.backend.dto.crm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class NoteDto {

    private Long id;
    private String kind;
    private String text;
    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;
}
