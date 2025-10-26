package com.example.chefbackend.dto;

import com.example.chefbackend.model.Chef;

public class ChefMapper {
    public static ChefDto toDto(Chef c) {
        return new ChefDto(
                c.getId(),
                c.getName(),
                c.getCuisine(),
                c.getPricePerPerson()
        );
    }
}
