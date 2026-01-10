package com.example.chefbackend.controller;

import com.example.chefbackend.dto.UserDetailsRequest;
import com.example.chefbackend.dto.UserDetailsResponse;
import com.example.chefbackend.model.UserDetails;
import com.example.chefbackend.service.UserDetailsService;
import com.example.chefbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-details")
@CrossOrigin(origins = "*")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long extractUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    @GetMapping
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromToken(authHeader);
            UserDetails details = userDetailsService.getUserDetails(userId);
            if (details == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
            }
            return ResponseEntity.ok(new UserDetailsResponse(details));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping
    public ResponseEntity<?> createUserDetails(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDetailsRequest request) {
        try {
            Long userId = extractUserIdFromToken(authHeader);
            UserDetails details = userDetailsService.createUserDetails(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserDetailsResponse(details));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUserDetails(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDetailsRequest request) {
        try {
            Long userId = extractUserIdFromToken(authHeader);
            UserDetails details = userDetailsService.updateUserDetails(userId, request);
            return ResponseEntity.ok(new UserDetailsResponse(details));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
