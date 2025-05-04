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
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    // Inject the path from application.properties
    @Value("${app.firebase.service-account-key-path:}") // Default to empty string if not set
    private String serviceAccountKeyPath;

    private FirebaseApp firebaseApp;

    @PostConstruct // Ensures this method runs after the bean is created and dependencies injected
    public void initializeFirebase() {
        try {
            // Try environment variable first (standard for deployment)
            String googleCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            InputStream serviceAccountStream = null;

            if (StringUtils.hasText(googleCredentials)) {
                logger.info("Initializing Firebase using GOOGLE_APPLICATION_CREDENTIALS environment variable.");
                // The SDK typically picks this up automatically if the variable is set,
                // but we can also load it explicitly if needed.
                // serviceAccountStream = new FileInputStream(googleCredentials);
                 FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    // Optionally set database URL if needed (usually inferred)
                    // .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com")
                    .build();
                  if (FirebaseApp.getApps().isEmpty()) { // Check if already initialized
                      this.firebaseApp = FirebaseApp.initializeApp(options);
                      logger.info("Firebase initialized successfully via Application Default Credentials.");
                  } else {
                      this.firebaseApp = FirebaseApp.getInstance(); // Get existing instance
                  }

            } else if (StringUtils.hasText(serviceAccountKeyPath)) {
                logger.info("Initializing Firebase using service account key file path: {}", serviceAccountKeyPath);
                serviceAccountStream = new FileInputStream(serviceAccountKeyPath);
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

                 if (FirebaseApp.getApps().isEmpty()) {
                    this.firebaseApp = FirebaseApp.initializeApp(options);
                    logger.info("Firebase initialized successfully via file path.");
                 } else {
                     this.firebaseApp = FirebaseApp.getInstance();
                 }
            } else {
                logger.warn("Firebase initialization skipped: Neither GOOGLE_APPLICATION_CREDENTIALS environment variable nor app.firebase.service-account-key-path property is set.");
                // Handle case where no credentials are provided, maybe try default instance?
                 try {
                     this.firebaseApp = FirebaseApp.getInstance();
                     logger.info("Using default FirebaseApp instance.");
                 } catch (IllegalStateException e) {
                     logger.error("No Firebase app has been initialized yet, and no credentials provided.");
                     throw new IOException("Failed to initialize Firebase: Credentials not found.", e);
                 }
            }

            if (serviceAccountStream != null) {
                serviceAccountStream.close();
            }

        } catch (IOException e) {
            logger.error("Failed to initialize Firebase Admin SDK", e);
            // Depending on your app, you might want to re-throw or exit
            // throw new RuntimeException(e);
        }
    }

    @Bean
    public Firestore getFirestore() {
        if (this.firebaseApp == null) {
             // Attempt initialization again if it failed earlier but might be available now
             // This is a fallback, ideally PostConstruct handles it.
             initializeFirebase();
             if (this.firebaseApp == null) {
                 throw new IllegalStateException("FirebaseApp has not been initialized. Cannot create Firestore bean.");
             }
        }
        return FirestoreClient.getFirestore(this.firebaseApp);
    }
} 