package com.grandcity.backend.controller;

import com.grandcity.backend.dto.RequestDto;
import com.grandcity.backend.dto.RequestStatusUpdateDto;
import com.grandcity.backend.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<RequestDto> create(@Valid @RequestBody RequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.create(dto));
    }

    @GetMapping
    public List<RequestDto> list() {
        return requestService.list();
    }

    @PatchMapping("/{id}")
    public RequestDto updateStatus(@PathVariable Long id, @Valid @RequestBody RequestStatusUpdateDto dto) {
        return requestService.updateStatus(id, dto.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
