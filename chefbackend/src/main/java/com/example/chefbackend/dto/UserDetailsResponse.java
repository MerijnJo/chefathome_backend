package com.example.chefbackend.dto;

import com.example.chefbackend.model.UserDetails;
import java.time.LocalDateTime;

public class UserDetailsResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private String bio;
    private String favouriteCuisine;
    private String dietaryRestrictions;
    private LocalDateTime updatedAt;

    public UserDetailsResponse(UserDetails details) {
        this.id = details.getId();
        this.firstName = details.getFirstName();
        this.lastName = details.getLastName();
        this.city = details.getCity();
        this.bio = details.getBio();
        this.favouriteCuisine = details.getFavouriteCuisine();
        this.dietaryRestrictions = details.getDietaryRestrictions();
        this.updatedAt = details.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFavouriteCuisine() {
        return favouriteCuisine;
    }

    public void setFavouriteCuisine(String favouriteCuisine) {
        this.favouriteCuisine = favouriteCuisine;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
