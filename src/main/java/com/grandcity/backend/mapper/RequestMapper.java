package com.grandcity.backend.mapper;

import com.grandcity.backend.dto.RequestDto;
import com.grandcity.backend.entity.ContactRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public RequestDto toDto(ContactRequest r) {
        RequestDto dto = new RequestDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setEmail(r.getEmail());
        dto.setPhone(r.getPhone());
        dto.setKind(r.getKind());
        dto.setMessage(r.getMessage());
        dto.setStatus(r.getStatus());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
