package com.big_hackathon.backend_v2.repo;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class FirebaseRepo {

    private Firestore repo;
    
    @Value("src\\main\\java\\com\\big_hackathon\\backend_v2\\keys\\cred.json")
    private Resource privateKey;

    public FirebaseRepo(){
            // Use a service account
            FileInputStream serviceAccount;
            try {
                InputStream credentials = new ByteArrayInputStream(privateKey.getContentAsByteArray());
                FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(credentials)).build();

                FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
                this.repo = FirestoreClient.getFirestore(firebaseApp);

            } catch (Exception e) {
                System.err.println("Error connecting to Firestore - this error should be handeled by error middleware");
                e.printStackTrace();
                this.repo=null;
            }


    }
    public Firestore getRepo(){
        return this.repo;
    }

}