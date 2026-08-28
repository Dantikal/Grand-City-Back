package com.grandcity.backend.mapper;

import com.grandcity.backend.dto.BookingDto;
import com.grandcity.backend.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setPropertyId(b.getPropertyId());
        dto.setName(b.getName());
        dto.setEmail(b.getEmail());
        dto.setPhone(b.getPhone());
        dto.setDate(b.getDate());
        dto.setMessage(b.getMessage());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}
