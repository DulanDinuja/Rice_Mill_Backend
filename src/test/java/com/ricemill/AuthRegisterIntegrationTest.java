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
import org.springframework.http.*;
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
class AuthRegisterIntegrationTest {

    private static MySQLContainer<?> mysql;

    @BeforeAll
    static void requireDocker() {
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for Testcontainers");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
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

        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .fullName("Admin")
                .roles(Set.of(UserRole.ADMIN))
                .build();
        admin.setActive(true);
        userRepository.save(admin);
    }

    @Test
    void registerShouldSucceedWithoutAuthentication() {
        // No login required - public registration
        AuthDto.RegisterRequest req = new AuthDto.RegisterRequest();
        req.setFullName("Public User");
        req.setIdNumber("555666777V");
        req.setMobileNumber("+94775556666");
        req.setEmail("publicuser@example.com");
        req.setPassword("Public@123");
        req.setConfirmPassword("Public@123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthDto.RegisterRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> registerResp = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(registerResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResp.getBody()).contains("publicuser@example.com");

        // Verify user was created with STAFF role
        User user = userRepository.findByEmail("publicuser@example.com").orElseThrow();
        assertThat(user.getActive()).isTrue();
        assertThat(user.getRoles()).contains(UserRole.STAFF);
        assertThat(user.getUsername()).isEqualTo("publicuser@example.com");
        assertThat(user.getIdNumber()).isEqualTo("555666777V");
        assertThat(user.getMobileNumber()).isEqualTo("+94775556666");
    }

    @Test
    void registerShouldSucceedForAnyAuthenticatedUser() {
        // login as non-admin (staff user)
        User staff = User.builder()
                .username("staff")
                .email("staff@example.com")
                .passwordHash(passwordEncoder.encode("staff123"))
                .fullName("Staff")
                .roles(Set.of(UserRole.STAFF))
                .build();
        staff.setActive(true);
        userRepository.save(staff);

        AuthDto.LoginRequest login = new AuthDto.LoginRequest();
        login.setUsernameOrEmail("staff");
        login.setPassword("staff123");

        ResponseEntity<String> loginResp = restTemplate.postForEntity("/api/auth/login", login, String.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).contains("accessToken");

        String accessToken = extractJsonString(loginResp.getBody(), "accessToken");

        AuthDto.RegisterRequest req = new AuthDto.RegisterRequest();
        req.setFullName("Test User");
        req.setIdNumber("123456789V");
        req.setMobileNumber("+94771234567");
        req.setEmail("testuser@example.com");
        req.setPassword("Test@12345");
        req.setConfirmPassword("Test@12345");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<AuthDto.RegisterRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> registerResp = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity,
                String.class
        );

        // Registration is now public, so it should succeed even for non-admin
        assertThat(registerResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void registerShouldSucceedForAdmin() {
        AuthDto.LoginRequest login = new AuthDto.LoginRequest();
        login.setUsernameOrEmail("admin");
        login.setPassword("admin123");

        ResponseEntity<String> loginResp = restTemplate.postForEntity("/api/auth/login", login, String.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        String accessToken = extractJsonString(loginResp.getBody(), "accessToken");

        AuthDto.RegisterRequest req = new AuthDto.RegisterRequest();
        req.setFullName("Test User Admin");
        req.setIdNumber("987654321V");
        req.setMobileNumber("+94779876543");
        req.setEmail("testuser2@example.com");
        req.setPassword("Test@12345");
        req.setConfirmPassword("Test@12345");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<AuthDto.RegisterRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> registerResp = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(registerResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResp.getBody()).contains("testuser2@example.com");
        // Username is now auto-generated from email
        assertThat(userRepository.findByEmail("testuser2@example.com").orElseThrow().getActive()).isTrue();
    }

    @Test
    void registerShouldFailWhenPasswordsDoNotMatch() {
        AuthDto.RegisterRequest req = new AuthDto.RegisterRequest();
        req.setFullName("Test User");
        req.setIdNumber("123456789V");
        req.setMobileNumber("+94771234567");
        req.setEmail("mismatch@example.com");
        req.setPassword("Test@12345");
        req.setConfirmPassword("DifferentPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthDto.RegisterRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> registerResp = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(registerResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(registerResp.getBody()).contains("Passwords do not match");
    }

    @Test
    void registerShouldFailWhenEmailAlreadyExists() {
        // First registration
        AuthDto.RegisterRequest req1 = new AuthDto.RegisterRequest();
        req1.setFullName("First User");
        req1.setIdNumber("111111111V");
        req1.setMobileNumber("+94771111111");
        req1.setEmail("duplicate@example.com");
        req1.setPassword("Test@12345");
        req1.setConfirmPassword("Test@12345");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthDto.RegisterRequest> entity1 = new HttpEntity<>(req1, headers);

        ResponseEntity<String> registerResp1 = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity1,
                String.class
        );
        assertThat(registerResp1.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Second registration with same email
        AuthDto.RegisterRequest req2 = new AuthDto.RegisterRequest();
        req2.setFullName("Second User");
        req2.setIdNumber("222222222V");
        req2.setMobileNumber("+94772222222");
        req2.setEmail("duplicate@example.com");
        req2.setPassword("Test@12345");
        req2.setConfirmPassword("Test@12345");

        HttpEntity<AuthDto.RegisterRequest> entity2 = new HttpEntity<>(req2, headers);

        ResponseEntity<String> registerResp2 = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity2,
                String.class
        );

        assertThat(registerResp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(registerResp2.getBody()).contains("Email already exists");
    }

    /**
     * Tiny helper to avoid pulling in a JSON parser in tests.
     */
    private static String extractJsonString(String json, String key) {
        // expects: "key":"value"
        String needle = "\"" + key + "\":";
        int i = json.indexOf(needle);
        if (i < 0) return null;
        int start = json.indexOf('"', i + needle.length());
        if (start < 0) return null;
        int end = json.indexOf('"', start + 1);
        if (end < 0) return null;
        return json.substring(start + 1, end);
    }
}

