package com.example.chefbackend.service;

import com.example.chefbackend.dto.RegisterRequest;
import com.example.chefbackend.model.User;
import com.example.chefbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$hashedpassword");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
    }

    @Test
    void login_WithValidCredentials_ReturnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        User result = userService.login("test@example.com", "password123");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
    }

    @Test
    void login_WithNonExistentEmail_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("nonexistent@example.com", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");

        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_WithInvalidPassword_ThrowsException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.login("test@example.com", "wrongpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", testUser.getPassword());
    }

    @Test
    void register_WithValidData_CreatesUserWithHashedPassword() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getPassword()).isEqualTo("$2a$10$hashedpassword");

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithDuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_PasswordIsHashedBeforeSaving() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(registerRequest);

        assertThat(result.getPassword()).isNotEqualTo("password123");
        assertThat(result.getPassword()).isEqualTo("$2a$10$hashedpassword");
        verify(passwordEncoder).encode("password123");
    }
}
