package com.chat.backend.utility;

import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean adminExists = userRepository.existsByRolesContaining("admin");

        if (!adminExists) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("admin");

            userRepository.save(admin);
            System.out.println("Created default admin account");
        }
    }
}
