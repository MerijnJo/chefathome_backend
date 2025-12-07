package com.example.chefbackend.mapper;

import com.example.chefbackend.dto.ChefDetailDto;
import com.example.chefbackend.dto.ChefSummaryDto;
import com.example.chefbackend.model.Chef;
import org.springframework.stereotype.Component;

@Component
public class ChefMapper {

    public ChefSummaryDto toSummaryDto(Chef chef) {
        return new ChefSummaryDto(
                chef.getId(),
                chef.getName(),
                chef.getProfilePicture(),
                chef.getExperience(),
                chef.getFoodOrigin(),
                chef.getExpertise(),
                chef.getBasePrice()
        );
    }

    public ChefDetailDto toDetailDto(Chef chef) {
        return new ChefDetailDto(
                chef.getId(),
                chef.getName(),
                chef.getProfilePicture(),
                chef.getExperience(),
                chef.getFoodOrigin(),
                chef.getExpertise(),
                chef.getBasePrice(),
                chef.getAbout(),
                chef.getSpecialties(),
                chef.getSetMenus(),
                chef.getDishGallery(),
                chef.getExtraInformation()
        );
    }

    public Chef toEntity(ChefDetailDto dto) {
        Chef chef = new Chef();
        chef.setId(dto.id());
        chef.setName(dto.name());
        chef.setProfilePicture(dto.profilePicture());
        chef.setExperience(dto.experience());
        chef.setFoodOrigin(dto.foodOrigin());
        chef.setExpertise(dto.expertise());
        chef.setBasePrice(dto.basePrice());
        chef.setAbout(dto.about());
        chef.setSpecialties(dto.specialties());
        chef.setSetMenus(dto.setMenus());
        chef.setDishGallery(dto.dishGallery());
        chef.setExtraInformation(dto.extraInformation());
        return chef;
    }
}
