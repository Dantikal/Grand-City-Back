package com.grandcity.backend.controller;

import com.grandcity.backend.dto.BookingDto;
import com.grandcity.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(@Valid @RequestBody BookingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(dto));
    }

    @GetMapping
    public List<BookingDto> list() {
        return bookingService.list();
    }
}
