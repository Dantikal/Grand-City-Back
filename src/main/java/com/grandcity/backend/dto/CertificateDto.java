package com.grandcity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {

    private String id;
    private String title;
    private String issuer;
    private String year;
    private String image;
}
