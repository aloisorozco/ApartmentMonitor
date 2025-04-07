package com.big_hackathon.backend_v2.repo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.Hasher;
import com.big_hackathon.backend_v2.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import lombok.SneakyThrows;

// collection = db entry that contains nothing more than documents
// document = "end point" that contains properties and other documents/collections

@Repository
public class UserDAO {

    private final Firestore db;
    private final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    // No need to use Autowired - Spring injects automatically for constructors - however, better to add for clarity.
    @Autowired
    public UserDAO(Firestore db){
        this.db = db;
    }

    @SneakyThrows
    public User saveUser(String email, String password, String fname, String lname){
        String emailHash = Hasher.hashData(email);
        String passwordHash = Hasher.hashData(password);
        
        DocumentSnapshot user = db.collection("users").document(emailHash).get().get();
        if(user.exists()){

            // TODO: throw custom error and handle it in error middelware
            logger.info("User " + email + " already exists");
            return null;
        }

        long createAt = System.currentTimeMillis() / 1000L;

        Map<String, Object> data = new HashMap<>();
        data.put("fname", fname);
        data.put("lname", lname);
        data.put("createdAt", createAt);
        data.put("email", email);
        data.put("password_hashed", passwordHash);

        WriteResult result = db.collection("users").document(emailHash).set(data).get();
        logger.info("Created new user " +  email + " at time " + result.getUpdateTime());

        return User.builder().email(email).passwordHashed(passwordHash).firstName(fname).lastName(lname).createdAt(createAt).build();
    }

    @SneakyThrows
    public boolean delUser(String email){
        String emailHash = Hasher.hashData(email);
        ApiFuture<WriteResult> res = db.collection("users").document(emailHash).delete();

        // TODO: this will throw an error in the event the .get() fails - we should handle this in error middleware
        WriteResult metadata = res.get();
        logger.info("Deleted user " +  email + " at time " + metadata.getUpdateTime());
        return true;
    }

    @SneakyThrows
    public boolean authUser(String password, String email){
        String userHash = Hasher.hashData(email);
        String passwordHash = Hasher.hashData(password);
        ApiFuture<DocumentSnapshot> userQuerry = db.collection("users").document(userHash).get();
        DocumentSnapshot userdoc = userQuerry.get();

        if(!userdoc.exists()){
            logger.info("User with email " + email + " does not exist in DB");
            return false;
        }

        String fetchedPasswordHashed = userdoc.getString("password_hashed");

        if(fetchedPasswordHashed.equals(passwordHash)){
            return true;
        }
        return false;
    }

    public void updateUser(){
        // TODO
    }

    @SneakyThrows
    public User getUser(String email){

        String hashedEamil = Hasher.hashData(email);
        DocumentSnapshot userDoc = db.collection("users").document(hashedEamil).get().get();
        if(!userDoc.exists()){
            logger.info("User " + email + " does not exist");
            return null;
        }

        String fname = userDoc.getString("fname");
        String lname = userDoc.getString("lname");
        String password = userDoc.getString("password_hashed");
        long createdAt = userDoc.getLong("createdAt");

        return User.builder().firstName(fname).lastName(lname).email(email).passwordHashed(password).createdAt(createdAt).build();
    }

    @SneakyThrows
    public void getUsers(){

        // .get() initiates an async read request to the db 
        ApiFuture<QuerySnapshot> query = db.collection("users").get();

        // we have another .get() here since we need to wait for the async API futuure read request
        // this get() here is BLOCKING - we are waiting until db resolves
        QuerySnapshot querySnapshot = query.get(); // QuerySnapshot = snapshot of docs in the collection
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
        }
    }
}
