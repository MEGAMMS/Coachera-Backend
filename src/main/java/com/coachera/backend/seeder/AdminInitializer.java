package com.coachera.backend.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.coachera.backend.entity.User;
import com.coachera.backend.repository.UserRepository;

@Configuration
public class AdminInitializer {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner createAdmin() {
        System.out.println("Befor return createAdmin"); // Debug line
        return args -> {
            System.out.println("Checking for admin user..."); // Debug line
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin"))
                        .role("ADMIN")
                        .isVerified(true)
                        .build();
                userRepo.save(admin);
                System.out.println("Admin user created!");
            } else {
                System.out.println("Admin user already exists."); // Debug line
            }
        };
    }
}
