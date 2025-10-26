package com.example.chefbackend.service;

import com.example.chefbackend.dto.ChefDto;
import com.example.chefbackend.dto.ChefMapper;
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

    // Seed a demo chef and return as DTO
    public ChefDto seed() {
        Chef c = new Chef("Luca", "Italian", 75);
        Chef saved = repo.save(c);
        return ChefMapper.toDto(saved);
    }

    // Return ALL chefs as DTOs (no entities)
    public List<ChefDto> findAll() {
        return repo.findAll()
                .stream()
                .map(ChefMapper::toDto)
                .toList();
    }
}
