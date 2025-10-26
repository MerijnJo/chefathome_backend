package com.example.chefbackend.controller;

import com.example.chefbackend.dto.ChefDto;
import com.example.chefbackend.service.ChefService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chefs")
@CrossOrigin(origins = {"http://localhost:3000"}) // adjust for your frontend
public class ChefController {
    private final ChefService service;

    public ChefController(ChefService service) {
        this.service = service;
    }

    @PostMapping("/seed")
    public ChefDto seed() {
        return service.seed();
    }

    @GetMapping
    public List<ChefDto> all() {
        return service.findAll();
    }
}
