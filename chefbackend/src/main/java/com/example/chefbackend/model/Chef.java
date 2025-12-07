package com.example.chefbackend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chefs")
public class Chef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // General view fields
    private String name;
    private String profilePicture; // URL or path to image
    private Integer experience; // in years

    @Column(length = 100)
    private String foodOrigin; // e.g., "Italian", "French fusion"

    @Column(length = 100)
    private String expertise; // e.g., "Mediterranean cuisine"

    private Integer basePrice; // base price per person

    // Detailed view fields
    @Column(length = 1000)
    private String about; // longer description

    @ElementCollection
    @CollectionTable(name = "chef_specialties", joinColumns = @JoinColumn(name = "chef_id"))
    @Column(name = "specialty", length = 200)
    @OrderColumn(name = "specialty_order")
    private List<String> specialties = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "chef_set_menus", joinColumns = @JoinColumn(name = "chef_id"))
    @Column(name = "menu", length = 500)
    @OrderColumn(name = "menu_order")
    private List<String> setMenus = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "chef_dish_gallery", joinColumns = @JoinColumn(name = "chef_id"))
    @Column(name = "dish_image", length = 500)
    @OrderColumn(name = "image_order")
    private List<String> dishGallery = new ArrayList<>();

    @Column(length = 1000)
    private String extraInformation;

    // Constructors
    public Chef() {}

    public Chef(String name, String profilePicture, Integer experience, String foodOrigin,
                String expertise, Integer basePrice) {
        this.name = name;
        this.profilePicture = profilePicture;
        this.experience = experience;
        this.foodOrigin = foodOrigin;
        this.expertise = expertise;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getFoodOrigin() {
        return foodOrigin;
    }

    public void setFoodOrigin(String foodOrigin) {
        this.foodOrigin = foodOrigin;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public List<String> getSetMenus() {
        return setMenus;
    }

    public void setSetMenus(List<String> setMenus) {
        this.setMenus = setMenus;
    }

    public List<String> getDishGallery() {
        return dishGallery;
    }

    public void setDishGallery(List<String> dishGallery) {
        this.dishGallery = dishGallery;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }
}
