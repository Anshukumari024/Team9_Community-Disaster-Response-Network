package com.example.cdrn.controller;

import com.example.cdrn.model.Incident;
import com.example.cdrn.service.IncidentService;
import com.example.cdrn.websocket.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentService service;
    private final NotificationService notifier;

    public IncidentController(IncidentService service, NotificationService notifier) {
        this.service = service; this.notifier = notifier;
    }

    @PostMapping
    public ResponseEntity<Incident> report(@Validated @RequestBody Incident dto) {
        var created = service.create(dto);
        notifier.broadcastIncident(created);
        return ResponseEntity.created(URI.create("/api/incidents/"+created.getId())).body(created);
    }

    @GetMapping
    public List<Incident> list() { return service.listAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> get(@PathVariable Long id) {
        var inc = service.get(id);
        return inc == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(inc);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(@PathVariable Long id, @RequestParam String status) {
        var inc = service.updateStatus(id, status);
        if (inc == null) return ResponseEntity.notFound().build();
        notifier.broadcastIncident(inc);
        return ResponseEntity.ok(inc);
    }
}
