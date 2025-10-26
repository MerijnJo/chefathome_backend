package com.example.chefbackend.dto;

public record ChefDto(
        Long id,
        String name,
        String cuisine,
        Integer pricePerPerson
) {}
