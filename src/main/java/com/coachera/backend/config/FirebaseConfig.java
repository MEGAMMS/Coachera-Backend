package com.coachera.backend.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfig {
    
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
    
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Load your service account key
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
        
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build();
            
        return FirebaseApp.initializeApp(options);
    }
}
