package com.grandcity.backend.controller;

import com.grandcity.backend.dto.CertificateDto;
import com.grandcity.backend.dto.CertificateOrderDto;
import com.grandcity.backend.service.CertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService service;

    public CertificateController(CertificateService service) {
        this.service = service;
    }

    @GetMapping
    public List<CertificateDto> list() {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<CertificateDto> create(@RequestBody CertificateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // /order must be declared before /{id} so Spring doesn't treat "order" as an id
    @PutMapping("/order")
    public ResponseEntity<Void> reorder(@RequestBody CertificateOrderDto body) {
        service.reorder(body.getIds());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public CertificateDto update(@PathVariable String id, @RequestBody CertificateDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
