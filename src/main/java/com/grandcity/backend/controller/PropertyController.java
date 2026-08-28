package com.grandcity.backend.controller;

import com.grandcity.backend.dto.PropertyDto;
import com.grandcity.backend.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public List<PropertyDto> list(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer beds,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Integer limit) {

        return propertyService.list(query, category, listingType, kind, minPrice, maxPrice, beds, sort, featured, limit);
    }

    @GetMapping("/{idOrSlug}")
    public PropertyDto getOne(@PathVariable String idOrSlug) {
        return propertyService.getByIdOrSlug(idOrSlug);
    }

    @PostMapping
    public ResponseEntity<PropertyDto> create(@Valid @RequestBody PropertyDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(dto));
    }

    @PutMapping("/{id}")
    public PropertyDto update(@PathVariable String id, @Valid @RequestBody PropertyDto dto) {
        return propertyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
