package com.chat.backend.utility;

import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsersInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.existsByRolesContaining("admin");
        boolean deletedUserExists = userRepository.existsByUsername("Deleted User [*]");

        if (!adminExists) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("admin");

            userRepository.save(admin);
            System.out.println("Created default admin account");
        }

        if (!deletedUserExists) {
            UserEntity deletedUser = new UserEntity();
            deletedUser.setUsername("Deleted User [*]");
            deletedUser.setPassword(passwordEncoder.encode("do not enter"));
            deletedUser.setRole("deleted");

            userRepository.save(deletedUser);
            System.out.println("Created default deleted user account");
        }
    }
}
