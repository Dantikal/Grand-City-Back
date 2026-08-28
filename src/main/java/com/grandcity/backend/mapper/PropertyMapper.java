package com.grandcity.backend.mapper;

import com.grandcity.backend.dto.CoordinatesDto;
import com.grandcity.backend.dto.PropertyDto;
import com.grandcity.backend.entity.Property;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class PropertyMapper {

    public PropertyDto toDto(Property p) {
        PropertyDto dto = new PropertyDto();
        dto.setId(p.getId());
        dto.setSlug(p.getSlug());
        dto.setTitle(p.getTitle());
        dto.setArea(p.getArea());
        dto.setCity(p.getCity());
        dto.setPrice(p.getPrice());
        dto.setRentPeriod(p.getRentPeriod());
        dto.setCategory(p.getCategory());
        dto.setListingType(p.getListingType());
        dto.setKind(p.getKind());
        dto.setStatus(p.getStatus());
        dto.setBeds(p.getBeds());
        dto.setBaths(p.getBaths());
        dto.setSqft(p.getSqft());
        dto.setImages(p.getImages() != null ? p.getImages() : new ArrayList<>());
        dto.setDescription(p.getDescription());
        dto.setFeatures(p.getFeatures() != null ? p.getFeatures() : new ArrayList<>());
        dto.setAgentId(p.getAgentId());
        dto.setCoordinates(new CoordinatesDto(p.getLat(), p.getLng()));
        dto.setFeatured(p.isFeatured());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    /** Applies dto fields onto an entity (used for both create and update). */
    public void applyToEntity(PropertyDto dto, Property entity) {
        entity.setTitle(dto.getTitle());
        entity.setArea(dto.getArea());
        entity.setCity(dto.getCity());
        entity.setPrice(dto.getPrice());
        entity.setRentPeriod(dto.getRentPeriod());
        entity.setCategory(dto.getCategory());
        entity.setListingType(dto.getListingType());
        entity.setKind(dto.getKind());
        entity.setStatus(dto.getStatus());
        entity.setBeds(dto.getBeds());
        entity.setBaths(dto.getBaths());
        entity.setSqft(dto.getSqft());
        entity.setImages(dto.getImages() != null ? dto.getImages() : new ArrayList<>());
        entity.setDescription(dto.getDescription());
        entity.setFeatures(dto.getFeatures() != null ? dto.getFeatures() : new ArrayList<>());
        entity.setAgentId(dto.getAgentId());
        if (dto.getCoordinates() != null) {
            entity.setLat(dto.getCoordinates().getLat());
            entity.setLng(dto.getCoordinates().getLng());
        } else {
            entity.setLat(BigDecimal.ZERO);
            entity.setLng(BigDecimal.ZERO);
        }
        entity.setFeatured(dto.isFeatured());
    }
}
