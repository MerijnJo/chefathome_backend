package com.example.chefbackend.service;

import com.example.chefbackend.dto.UserDetailsRequest;
import com.example.chefbackend.model.User;
import com.example.chefbackend.model.UserDetails;
import com.example.chefbackend.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserService userService;

    public UserDetails getUserDetails(Long userId) {
        return userDetailsRepository.findByUserId(userId).orElse(null);
    }

    public UserDetails createUserDetails(Long userId, UserDetailsRequest request) {
        if (userDetailsRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User details already exist");
        }

        User user = userService.findById(userId);

        UserDetails details = new UserDetails();
        details.setUser(user);
        updateDetailsFromRequest(details, request);

        return userDetailsRepository.save(details);
    }

    public UserDetails updateUserDetails(Long userId, UserDetailsRequest request) {
        UserDetails details = userDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User details not found"));

        updateDetailsFromRequest(details, request);
        return userDetailsRepository.save(details);
    }

    private void updateDetailsFromRequest(UserDetails details, UserDetailsRequest request) {
        details.setFirstName(request.getFirstName());
        details.setLastName(request.getLastName());
        details.setCity(request.getCity());
        details.setBio(request.getBio());
        details.setFavouriteCuisine(request.getFavouriteCuisine());
        details.setDietaryRestrictions(request.getDietaryRestrictions());
    }
}
