package com.example.chefbackend.service;

import com.example.chefbackend.model.User;
import com.example.chefbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for user management operations
 * Handles user registration, authentication, and password hashing
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     * Validates uniqueness of email and username, hashes password
     */
    public User registerUser(String email, String username, String password) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Create new user with hashed password
        User user = new User(email, username, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Verify user password for login
     */
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
