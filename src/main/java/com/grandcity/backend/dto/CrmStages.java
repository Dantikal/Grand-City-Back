package com.grandcity.backend.dto;

import java.util.List;

/** Pipeline stages of a CRM lead, in board order. */
public final class CrmStages {

    public static final String PATTERN = "new|in-progress|meeting|contract|won|lost";

    /** Stages that end a lead — it no longer counts toward an employee's load. */
    public static final List<String> CLOSED = List.of("won", "lost");

    private CrmStages() {
    }
}
