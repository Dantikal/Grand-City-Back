package com.grandcity.backend.controller;

import com.grandcity.backend.dto.AgentDto;
import com.grandcity.backend.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public List<AgentDto> list() {
        return agentService.list();
    }

    @GetMapping("/{idOrSlug}")
    public AgentDto getOne(@PathVariable String idOrSlug) {
        return agentService.getByIdOrSlug(idOrSlug);
    }

    @PostMapping
    public ResponseEntity<AgentDto> create(@Valid @RequestBody AgentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.create(dto));
    }

    @PutMapping("/{id}")
    public AgentDto update(@PathVariable String id, @Valid @RequestBody AgentDto dto) {
        return agentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
