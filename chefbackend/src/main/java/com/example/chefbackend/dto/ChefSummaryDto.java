package com.example.chefbackend.dto;

// For general chef list view
public record ChefSummaryDto(
        Long id,
        String name,
        String profilePicture,
        Integer experience,
        String foodOrigin,
        String expertise,
        Integer basePrice
) {}
