package com.big_hackathon.backend_v2.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
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
    public void fetchWatchlist(String userID, String email){
        // ApiFuture<QuerySnapshot> query = db.collection("users").get()
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

    private String hashData(){
        // TODO: implement hashing function - we could use guava from google as a dependency
        return null;
    }
}
