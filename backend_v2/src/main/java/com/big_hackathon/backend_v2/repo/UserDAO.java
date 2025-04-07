package com.big_hackathon.backend_v2.repo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.big_hackathon.backend_v2.model.Apartment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ch.qos.logback.core.joran.action.AppenderAction;
import lombok.SneakyThrows;

// collection = db entry that contains nothing more than documents
// document = "end point" that contains properties and other documents/collections

@Repository
public class UserDAO {

    private final Firestore db;
    private final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    // No need to use Autowired - Spring injects automatically for constructors.
    public UserDAO(Firestore db){
        this.db = db;
    }

    public void saveUser(){
        // TODO
    }

    @SneakyThrows
    public void delUser(String userID){
        ApiFuture<WriteResult> res = db.collection("users").document("userID").delete();

        // TODO: this will throw an error in the event the .get() fails - we should handle this in error middleware
        WriteResult metadata = res.get();
        logger.info("Deleted user " +  userID + " at time " + metadata.getUpdateTime());
    }
    public void authUser(){
        // TODO
    }
    public void updateUser(){
        // TODO
    }

    @SneakyThrows
    public void getUser(){

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

    @SneakyThrows
    public static String hashData(String data){
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hexString = hashByte2Hex(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
        return hexString;
    }

    // translating byte hash to hex 
    private static String hashByte2Hex(byte[] hash){
        
        // size of 2 * len of hash since every byte becomes a two char hex value
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0'); // ex: hex = a, we add 0 in front to keep the hex num two char long
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
