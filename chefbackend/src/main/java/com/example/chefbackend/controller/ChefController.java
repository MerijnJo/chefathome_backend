package com.example.chefbackend.controller;

import com.example.chefbackend.dto.ChefDetailDto;
import com.example.chefbackend.dto.ChefFilterDto;
import com.example.chefbackend.dto.ChefSummaryDto;
import com.example.chefbackend.event.ChefViewedEvent;
import com.example.chefbackend.service.ChefService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chefs")
@CrossOrigin(origins = "*")
public class ChefController {

    private final ChefService chefService;
    private final ApplicationEventPublisher eventPublisher;

    public ChefController(ChefService chefService, ApplicationEventPublisher eventPublisher) {
        this.chefService = chefService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public ResponseEntity<List<ChefSummaryDto>> getChefs(
            @RequestParam(required = false) String foodOrigin,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minBasePrice,
            @RequestParam(required = false) Integer maxBasePrice,
            @RequestParam(required = false) String sortBy
    ) {
        ChefFilterDto filter = new ChefFilterDto(
                foodOrigin, expertise, minExperience, maxExperience, minBasePrice, maxBasePrice
        );
        List<ChefSummaryDto> chefs = chefService.getFilteredChefs(filter, sortBy);
        return ResponseEntity.ok(chefs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChefDetailDto> getChefById(@PathVariable Long id) {
        // Publish event for view tracking (async processing)
        eventPublisher.publishEvent(new ChefViewedEvent(id));

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
