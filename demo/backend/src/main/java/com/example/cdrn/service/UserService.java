package com.example.cdrn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cdrn.model.AppUser;
import com.example.cdrn.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) { this.repo = repo; }

    public AppUser register(AppUser user) {
        user.setVerified(false);
        return repo.save(user);
    }

    public List<AppUser> listAll() { return repo.findAll(); }

    public AppUser findByUsername(String username) { return repo.findByUsername(username).orElse(null); }
}
