package com.hopedale.hdalepark;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("Using already initialized FirebaseApp instance.");
            return FirebaseApp.getInstance();
        }

        String base64Config = System.getenv("FIREBASE_CONFIG_B64");
        if (base64Config == null || base64Config.isEmpty()) {
            throw new IOException("FIREBASE_CONFIG_B64 environment variable is missing or empty.");
        }

        logger.info("Decoding Firebase service account from FIREBASE_CONFIG_B64...");

        byte[] decodedBytes = Base64.getDecoder().decode(base64Config);
        File tempFile = File.createTempFile("firebase-service", ".json");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(decodedBytes);
        }

        try (FileInputStream serviceAccount = new FileInputStream(tempFile)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            logger.info("Initializing FirebaseApp with decoded service account.");
            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        logger.info("Creating Firestore bean.");
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
