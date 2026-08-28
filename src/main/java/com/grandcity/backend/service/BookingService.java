package com.grandcity.backend.service;

import com.grandcity.backend.dto.BookingDto;
import com.grandcity.backend.entity.Booking;
import com.grandcity.backend.mapper.BookingMapper;
import com.grandcity.backend.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    public BookingService(BookingRepository repository, BookingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public BookingDto create(BookingDto dto) {
        Booking entity = Booking.builder()
                .propertyId(dto.getPropertyId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .date(dto.getDate())
                .message(dto.getMessage())
                .createdAt(OffsetDateTime.now())
                .build();
        return mapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<BookingDto> list() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
