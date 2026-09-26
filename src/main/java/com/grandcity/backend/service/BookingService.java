package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.BadRequestException;
import com.grandcity.backend.dto.BookingDto;
import com.grandcity.backend.entity.Booking;
import com.grandcity.backend.mapper.BookingMapper;
import com.grandcity.backend.repository.BookingRepository;
import com.grandcity.backend.repository.PropertyRepository;
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
    private final RequestService requests;
    private final PropertyRepository properties;

    public BookingService(BookingRepository repository, BookingMapper mapper,
                          RequestService requests, PropertyRepository properties) {
        this.repository = repository;
        this.mapper = mapper;
        this.requests = requests;
        this.properties = properties;
    }

    @Transactional
    public BookingDto create(BookingDto dto) {
        if (!properties.existsById(dto.getPropertyId())) {
            throw new BadRequestException("Unknown property: " + dto.getPropertyId());
        }
        Booking entity = Booking.builder()
                .propertyId(dto.getPropertyId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .date(dto.getDate())
                .message(dto.getMessage())
                .createdAt(OffsetDateTime.now())
                .build();
        Booking saved = repository.save(entity);

        // Every viewing request also enters the CRM pipeline as a lead.
        String property = properties.findById(saved.getPropertyId())
                .map(p -> p.getTitle()).orElse(saved.getPropertyId());
        String note = saved.getMessage() != null && !saved.getMessage().isBlank() ? "\n" + saved.getMessage() : "";
        requests.createLead(saved.getName(), saved.getEmail(), saved.getPhone(), "viewing",
                "Запись на просмотр: " + property + ", дата: " + saved.getDate() + "." + note,
                saved.getPropertyId(), null);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> list() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
