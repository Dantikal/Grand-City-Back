package com.grandcity.backend.controller;

import com.grandcity.backend.dto.PresentationDto;
import com.grandcity.backend.service.PresentationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/presentations")
public class PresentationController {

    private final PresentationService service;

    public PresentationController(PresentationService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, PresentationDto> getAll() {
        return service.getAll();
    }

    @PutMapping("/{lang}")
    public PresentationDto save(@PathVariable String lang, @RequestBody PresentationDto dto) {
        return service.save(lang, dto);
    }

    @DeleteMapping("/{lang}")
    public ResponseEntity<Void> delete(@PathVariable String lang) {
        service.delete(lang);
        return ResponseEntity.noContent().build();
    }
}
