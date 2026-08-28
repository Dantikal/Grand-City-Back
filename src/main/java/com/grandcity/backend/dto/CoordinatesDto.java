package com.grandcity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDto {
    private BigDecimal lat;
    private BigDecimal lng;
}
