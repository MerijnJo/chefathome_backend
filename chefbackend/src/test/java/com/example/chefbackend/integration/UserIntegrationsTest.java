package com.example.chefbackend.integration;

import com.example.chefbackend.ChefbackendApplication;
import com.example.chefbackend.config.TestSecurityConfig;
import com.example.chefbackend.dto.LoginRequest;
import com.example.chefbackend.dto.LoginResponse;
import com.example.chefbackend.dto.RegisterRequest;
import com.example.chefbackend.dto.UserResponse;
import com.example.chefbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ChefbackendApplication.class
)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@Testcontainers
class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");  // ✅ Force PostgreSQL
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");  // ✅ Force PostgreSQL
        registry.add("spring.flyway.enabled", () -> "false");
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
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("testuser");
        assertThat(response.getBody().getUser().getEmail()).isEqualTo("test@example.com");

        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        registerUser("loginuser", "login@example.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("Password123!");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginRequest,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser().getEmail()).isEqualTo("login@example.com");
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        registerUser("wrongpassuser", "wrongpass@example.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongpass@example.com");
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
        registerUser("duplicate", "duplicate@example.com");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicate");
        request.setEmail("duplicate@example.com");
        request.setPassword("Password123!");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldGetAllUsers() {
        registerUser("user1", "user1@example.com");
        registerUser("user2", "user2@example.com");

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
        UserResponse user = registerUser("getbyiduser", "getbyid@example.com");

        ResponseEntity<UserResponse> response = restTemplate.getForEntity(
                baseUrl + "/" + user.getId(),
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("getbyiduser");
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
