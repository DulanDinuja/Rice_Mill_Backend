package com.ricemill.config;

import com.ricemill.entity.User;
import com.ricemill.entity.UserRole;
import com.ricemill.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.username}")
    private String adminUsername;
    
    @Value("${app.admin.password}")
    private String adminPassword;
    
    @Value("${app.admin.email}")
    private String adminEmail;

    /**
     * Dev-safety valve: if true, we will update the existing admin user's password to match
     * {@code app.admin.password} on startup.
     */
    @Value("${app.admin.force-password-sync:false}")
    private boolean forcePasswordSync;

    @Override
    public void run(String... args) {
        userRepository.findByUsername(adminUsername).ifPresentOrElse(existing -> {
            boolean needsPasswordUpdate = !passwordEncoder.matches(adminPassword, existing.getPasswordHash());
            boolean needsEmailUpdate = adminEmail != null && !adminEmail.equalsIgnoreCase(existing.getEmail());

            if (forcePasswordSync && needsPasswordUpdate) {
                existing.setPasswordHash(passwordEncoder.encode(adminPassword));
                userRepository.save(existing);
                log.info("Admin user password updated from configuration: {}", adminUsername);
            } else {
                log.info("Admin user already exists");
            }

            if (needsEmailUpdate) {
                existing.setEmail(adminEmail);
                userRepository.save(existing);
                log.info("Admin user email updated from configuration: {}", adminUsername);
            }
        }, () -> {
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .fullName("System Administrator")
                    .roles(Set.of(UserRole.ADMIN))
//                    .active(true)
                    .build();
            
            userRepository.save(admin);
            log.info("Admin user created: {}", adminUsername);
        });
    }
}
