package com.example.chefbackend.controller;

import com.example.chefbackend.dto.ChefDetailDto;
import com.example.chefbackend.dto.ChefSummaryDto;
import com.example.chefbackend.dto.ChefSummaryDto;
import com.example.chefbackend.service.ChefService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chefs")
@CrossOrigin(origins = "*")
public class ChefController {

    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    @GetMapping
    public ResponseEntity<List<ChefSummaryDto>> getAllChefs() {
        return ResponseEntity.ok(chefService.getAllChefsSummary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChefDetailDto> getChefById(@PathVariable Long id) {
        return ResponseEntity.ok(chefService.getChefById(id));
    }

    @PostMapping
    public ResponseEntity<ChefDetailDto> createChef(@RequestBody ChefDetailDto chefDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chefService.createChef(chefDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChefDetailDto> updateChef(@PathVariable Long id, @RequestBody ChefDetailDto chefDto) {
        return ResponseEntity.ok(chefService.updateChef(id, chefDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChef(@PathVariable Long id) {
        chefService.deleteChef(id);
        return ResponseEntity.noContent().build();
    }
}
