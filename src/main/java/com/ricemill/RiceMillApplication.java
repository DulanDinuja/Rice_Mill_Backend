package com.ricemill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RiceMillApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiceMillApplication.class, args);
    }
}