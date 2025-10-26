package com.example.chefbackend.controller;

import com.example.chefbackend.model.Chef;
import com.example.chefbackend.service.ChefService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chefs")
public class ChefController {
    private final ChefService service;

    public ChefController(ChefService service) {
        this.service = service;
    }

    @PostMapping("/seed")
    public Chef seed() {
        return service.seed();
    }

    @GetMapping
    public List<Chef> all() {
        return service.findAll();
    }
}
