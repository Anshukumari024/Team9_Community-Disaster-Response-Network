package com.example.cdrn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cdrn.model.Incident;
import com.example.cdrn.repository.IncidentRepository;

@Service
public class IncidentService {
    private final IncidentRepository repo;

    public IncidentService(IncidentRepository repo) {
        this.repo = repo;
    }

    public Incident create(Incident incident) {
        incident.setStatus("REPORTED");
        return repo.save(incident);
    }

    public List<Incident> listAll() { return repo.findAll(); }

    public Incident get(Long id) { return repo.findById(id).orElse(null); }

    public Incident updateStatus(Long id, String status) {
        var inc = repo.findById(id).orElse(null);
        if (inc == null) return null;
        inc.setStatus(status);
        return repo.save(inc);
    }
}
