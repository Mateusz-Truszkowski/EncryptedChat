package com.chat.backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
@Log
public class BackendApplication implements CommandLineRunner {

    private final DataSource dataSource;

    public BackendApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);

    }

    @Override
    public void run(final String... args) {
        final JdbcTemplate restTemplate = new JdbcTemplate(dataSource);
        restTemplate.execute("SELECT 1");
    }

    @PostConstruct
    public void init() {
        try (FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase/firebase-config.json")) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}