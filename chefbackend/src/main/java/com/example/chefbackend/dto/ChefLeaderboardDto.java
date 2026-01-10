package com.example.chefbackend.dto;

public record ChefLeaderboardDto(
        Long id,
        String name,
        String profilePicture,
        Integer viewCount
) {}
