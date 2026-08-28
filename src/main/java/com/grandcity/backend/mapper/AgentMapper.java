package com.grandcity.backend.mapper;

import com.grandcity.backend.dto.AgentDto;
import com.grandcity.backend.entity.Agent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class AgentMapper {

    public AgentDto toDto(Agent a) {
        AgentDto dto = new AgentDto();
        dto.setId(a.getId());
        dto.setSlug(a.getSlug());
        dto.setName(a.getName());
        dto.setRole(a.getRole());
        dto.setBio(a.getBio());
        dto.setLongBio(a.getLongBio());
        dto.setPhoto(a.getPhoto());
        dto.setEmail(a.getEmail());
        dto.setPhone(a.getPhone());
        dto.setSpecialties(a.getSpecialties() != null ? a.getSpecialties() : new ArrayList<>());
        dto.setAreas(a.getAreas() != null ? a.getAreas() : new ArrayList<>());
        dto.setSalesCount(a.getSalesCount());
        dto.setRating(a.getRating());
        dto.setSince(a.getSince());
        return dto;
    }

    public void applyToEntity(AgentDto dto, Agent entity) {
        entity.setName(dto.getName());
        entity.setRole(dto.getRole());
        entity.setBio(dto.getBio() != null ? dto.getBio() : "");
        entity.setLongBio(dto.getLongBio() != null ? dto.getLongBio() : "");
        entity.setPhoto(dto.getPhoto() != null ? dto.getPhoto() : "");
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone() != null ? dto.getPhone() : "");
        entity.setSpecialties(dto.getSpecialties() != null ? dto.getSpecialties() : new ArrayList<>());
        entity.setAreas(dto.getAreas() != null ? dto.getAreas() : new ArrayList<>());
        entity.setSalesCount(dto.getSalesCount() != null ? dto.getSalesCount() : 0);
        entity.setRating(dto.getRating() != null ? dto.getRating() : BigDecimal.ZERO);
        entity.setSince(dto.getSince());
    }
}
