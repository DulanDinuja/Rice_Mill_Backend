package com.ricemill;

import com.ricemill.dto.AuthDto;
import com.ricemill.entity.User;
import com.ricemill.entity.UserRole;
import com.ricemill.repository.UserRepository;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthIntegrationTest {

    private static MySQLContainer<?> mysql;

    @BeforeAll
    static void requireDocker() {
        // If Docker isn't available (common on CI/local setups without Docker Desktop), skip this test class.
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for Testcontainers");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Lazily initialize so we don't touch Docker at class-load time.
        if (mysql == null) {
            mysql = new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");
            mysql.start();
        }

        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .roles(Set.of(UserRole.STAFF))
//                .active(true)
                .build();

        userRepository.save(testUser);
    }

    @Test
    void shouldLoginSuccessfully() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("accessToken");
        assertThat(response.getBody()).contains("refreshToken");
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
