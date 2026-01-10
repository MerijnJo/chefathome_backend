package com.example.chefbackend.controller;

import com.example.chefbackend.dto.LoginRequest;
import com.example.chefbackend.dto.RegisterRequest;
import com.example.chefbackend.model.User;
import com.example.chefbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private LoginRequest validLoginRequest;
    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setCreatedAt(LocalDateTime.now());

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail("newuser@example.com");
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setPassword("password123");
    }

    @Test
    void login_WithValidCredentials_ReturnsUserResponse() throws Exception {
        when(userService.login(anyString(), anyString())).thenReturn(testUser);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.createdAt").exists());
    }

    @Test
    void login_WithInvalidEmail_ReturnsUnauthorized() throws Exception {
        when(userService.login(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    void login_WithInvalidPassword_ReturnsUnauthorized() throws Exception {
        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setEmail("test@example.com");
        wrongPasswordRequest.setPassword("wrongpassword");

        when(userService.login(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    void register_WithValidData_ReturnsCreatedUserResponse() throws Exception {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail(validRegisterRequest.getEmail());
        newUser.setUsername(validRegisterRequest.getUsername());
        newUser.setCreatedAt(LocalDateTime.now());

        when(userService.register(any(RegisterRequest.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.user.username").value("newuser"));
    }

    @Test
    void register_WithDuplicateEmail_ReturnsConflict() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already exists"));
    }
}
