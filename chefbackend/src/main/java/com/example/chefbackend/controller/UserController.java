package com.example.chefbackend.controller;

import com.example.chefbackend.dto.LoginRequest;
import com.example.chefbackend.dto.RegisterRequest;
import com.example.chefbackend.dto.UserResponse;
import com.example.chefbackend.model.User;
import com.example.chefbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for user authentication and management
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getEmail(),
                    request.getUsername(),
                    request.getPassword()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UserResponse(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Login user
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userService.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        User user = userOptional.get();
        if (!userService.verifyPassword(user, request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        return ResponseEntity.ok(new UserResponse(user));
    }

    /**
     * Get user by username
     * GET /api/users/{username}
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        return ResponseEntity.ok(new UserResponse(userOptional.get()));
    }
}
