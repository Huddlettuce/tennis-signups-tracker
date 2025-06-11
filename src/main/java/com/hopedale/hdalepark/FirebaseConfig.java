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

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase.service-account-key-path}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            logger.info("Initializing Firebase with service account: {}", serviceAccountKeyPath);

            FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } else {
            logger.info("Using already initialized FirebaseApp instance.");
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        logger.info("Creating Firestore bean.");
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
