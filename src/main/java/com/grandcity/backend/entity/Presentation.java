package com.grandcity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "presentations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presentation {

    @Id
    @Column(name = "lang", length = 10)
    private String lang;

    @Column(name = "url", columnDefinition = "text", nullable = false)
    private String url;

    @Column(name = "name", length = 200, nullable = false)
    private String name;
}
