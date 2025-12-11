package com.example.chefbackend.dto;

import java.util.List;

// For detailed chef view
public record ChefDetailDto(
        Long id,
        String name,
        String profilePicture,
        Integer experience,
        String foodOrigin,
        String expertise,
        Integer basePrice,
        String about,
        List<String> specialties,
        List<String> setMenus,
        List<String> dishGallery,
        String extraInformation
        // trying to add reviews later
) {}
