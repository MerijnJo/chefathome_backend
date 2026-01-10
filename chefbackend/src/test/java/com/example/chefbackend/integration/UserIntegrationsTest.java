package com.example.chefbackend.integration;

import com.example.chefbackend.dto.LoginRequest;
import com.example.chefbackend.dto.LoginResponse;
import com.example.chefbackend.dto.RegisterRequest;
import com.example.chefbackend.dto.UserResponse;
import com.example.chefbackend.model.User;
import com.example.chefbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("chefathome_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser_WithRealDatabase() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("integrationtest");
        request.setEmail("integration@test.com");
        request.setPassword("SecurePass123!");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser()).isNotNull();
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("integrationtest");

        User savedUser = userRepository.findByUsername("integrationtest").orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.getPassword()).isNotEqualTo("SecurePass123!");
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("logintest");
        registerRequest.setEmail("login@test.com");
        registerRequest.setPassword("Password123!");

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, LoginResponse.class);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@test.com");
        loginRequest.setPassword("Password123!");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginRequest,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser()).isNotNull();
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("logintest");
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("failtest");
        registerRequest.setEmail("fail@test.com");
        registerRequest.setPassword("CorrectPass123!");

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, LoginResponse.class);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("fail@test.com");
        loginRequest.setPassword("WrongPassword!");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldNotRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicate");
        request.setEmail("first@test.com");
        request.setPassword("Pass123!");

        restTemplate.postForEntity(baseUrl + "/register", request, LoginResponse.class);

        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("duplicate");
        duplicateRequest.setEmail("second@test.com");
        duplicateRequest.setPassword("Pass456!");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register",
                duplicateRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldGetAllUsers() {
        registerUser("user1", "user1@test.com");
        registerUser("user2", "user2@test.com");

        ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldGetUserById() {
        UserResponse registered = registerUser("getbyid", "getbyid@test.com");

        ResponseEntity<UserResponse> response = restTemplate.getForEntity(
                baseUrl + "/" + registered.getId(),
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("getbyid");
    }

    @Test
    void shouldReturn404ForNonExistentUser() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/99999",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private UserResponse registerUser(String username, String email) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword("Password123!");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                LoginResponse.class
        );

        return response.getBody().getUser();
    }
}
