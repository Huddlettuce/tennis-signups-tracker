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
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private static final String ENV_JSON  = "GOOGLE_APPLICATION_CREDENTIALS_JSON"; // raw JSON
    private static final String ENV_PATH  = "GOOGLE_APPLICATION_CREDENTIALS";      // file path

    // Optional local/property fallback
    @Value("${app.firebase.service-account-key-path:}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("Using existing FirebaseApp instance.");
            return FirebaseApp.getInstance();
        }

        // 1) Prefer raw JSON from env var (Render-friendly)
        String json = System.getenv(ENV_JSON);
        if (StringUtils.hasText(json)) {
            log.info("Initializing Firebase from {}", ENV_JSON);
            try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                GoogleCredentials creds = GoogleCredentials.fromStream(is);
                FirebaseOptions opts = FirebaseOptions.builder().setCredentials(creds).build();
                return FirebaseApp.initializeApp(opts);
            }
        }

        // 2) Otherwise, a file path via standard Google env var
        String envPath = System.getenv(ENV_PATH);
        if (StringUtils.hasText(envPath)) {
            log.info("Initializing Firebase from {} (file path).", ENV_PATH);
            try (InputStream is = new FileInputStream(envPath)) {
                GoogleCredentials creds = GoogleCredentials.fromStream(is);
                FirebaseOptions opts = FirebaseOptions.builder().setCredentials(creds).build();
                return FirebaseApp.initializeApp(opts);
            }
        }

        // 3) Or a file path from Spring property
        if (StringUtils.hasText(serviceAccountKeyPath)) {
            log.info("Initializing Firebase from app.firebase.service-account-key-path: {}", serviceAccountKeyPath);
            try (InputStream is = new FileInputStream(serviceAccountKeyPath)) {
                GoogleCredentials creds = GoogleCredentials.fromStream(is);
                FirebaseOptions opts = FirebaseOptions.builder().setCredentials(creds).build();
                return FirebaseApp.initializeApp(opts);
            }
        }

        // 4) Last resort: ADC (works on GCP, some CI/CDs)
        log.info("No explicit credentials provided. Trying Application Default Credentials.");
        GoogleCredentials creds = GoogleCredentials.getApplicationDefault();
        FirebaseOptions opts = FirebaseOptions.builder().setCredentials(creds).build();
        return FirebaseApp.initializeApp(opts);
    }

    @Bean
    public Firestore getFirestore(FirebaseApp app) {
        log.info("Creating Firestore bean.");
        return FirestoreClient.getFirestore(app);
    }
}
