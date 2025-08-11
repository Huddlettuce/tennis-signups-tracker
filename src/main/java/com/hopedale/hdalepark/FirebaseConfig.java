package com.hopedale.hdalepark;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    // Keep this for local dev only. On Render we’ll use GOOGLE_APPLICATION_CREDENTIALS.
    @Value("${app.firebase.service-account-key-path:}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("Using already initialized FirebaseApp instance.");
            return FirebaseApp.getInstance();
        }

        // Prefer GOOGLE_APPLICATION_CREDENTIALS (Render path we set at runtime)
        String gac = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (gac != null && !gac.isBlank()) {
            logger.info("Initializing Firebase via GOOGLE_APPLICATION_CREDENTIALS at: {}", gac);
            try (InputStream in = new FileInputStream(gac)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                return FirebaseApp.initializeApp(options);
            }
        }

        // Fallback: local path from application.properties (for local dev)
        if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isBlank()) {
            logger.info("Initializing Firebase via local file path: {}", serviceAccountKeyPath);
            try (InputStream in = new FileInputStream(serviceAccountKeyPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                return FirebaseApp.initializeApp(options);
            }
        }

        throw new IllegalStateException(
                "No Firebase credentials found. Set GOOGLE_APPLICATION_CREDENTIALS (Render) or configure app.firebase.service-account-key-path (local).");
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        logger.info("Creating Firestore bean.");
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
