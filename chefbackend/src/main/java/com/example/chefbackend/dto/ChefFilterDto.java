package com.example.chefbackend.dto;

public record ChefFilterDto(
        String foodOrigin,
        String expertise,
        Integer minExperience,
        Integer maxExperience,
        Integer minBasePrice,
        Integer maxBasePrice
) {}
