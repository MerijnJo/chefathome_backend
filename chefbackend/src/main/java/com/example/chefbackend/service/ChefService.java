package com.example.chefbackend.service;

import com.example.chefbackend.model.Chef;
import com.example.chefbackend.repository.ChefRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChefService {
    private final ChefRepository repo;

    public ChefService(ChefRepository repo) {
        this.repo = repo;
    }

    public Chef seed() {
        Chef c = new Chef("Luca", "Italian", 75);
        return repo.save(c);
    }

    public List<Chef> findAll() {
        return repo.findAll();
    }
}
