package com.big_hackathon.backend_v2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import lombok.SneakyThrows;

import java.io.InputStream;

// No need forpackage scan since we are inside the main package
@Configuration
public class FirestoreConfig {

    @Bean
    @SneakyThrows
    public Firestore firestore(){

        Resource resource = new ClassPathResource("cred.json");
        InputStream credentialsStream = resource.getInputStream();
        FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(credentialsStream)).build();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
        return FirestoreClient.getFirestore(firebaseApp);
    }

}
