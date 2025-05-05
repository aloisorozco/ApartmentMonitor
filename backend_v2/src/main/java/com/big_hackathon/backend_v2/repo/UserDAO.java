package com.big_hackathon.backend_v2.repo;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.big_hackathon.backend_v2.model.Hasher;
import com.big_hackathon.backend_v2.model.User;
import lombok.SneakyThrows;

// collection = db entry that contains nothing more than documents
// document = "end point" that contains properties and other documents/collections

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
//    private final Firestore db;
//    private final Logger logger = LoggerFactory.getLogger(UserDAO.class);
//
//    // No need to use Autowired - Spring injects automatically for constructors - however, better to add for clarity.
//    @Autowired
//    public UserDAO(Firestore db){
//        this.db = db;
//    }
//
//    @SneakyThrows
//    public boolean saveUser(User user){
//        String emailHash = Hasher.hashData(user.getEmail());
//
//        DocumentSnapshot userDoc = db.collection("users").document(emailHash).get().get();
//        if(userDoc.exists()){
//            // TODO: throw custom error and handle it in error middelware
//            logger.info("User " + user.getEmail() + " already exists");
//            return false;
//        }
//
//
//        WriteResult result = db.collection("users").document(emailHash).set(user).get();
//        logger.info("Created new user " +  user.getEmail() + " at time " + result.getUpdateTime());
//
//        return true;
//    }
//
//    @SneakyThrows
//    public boolean delUser(String email){
//        String emailHash = Hasher.hashData(email);
//        ApiFuture<WriteResult> res = db.collection("users").document(emailHash).delete();
//
//        // TODO: this will throw an error in the event the .get() fails - we should handle this in error middleware
//        WriteResult metadata = res.get();
//        logger.info("Deleted user " +  email + " at time " + metadata.getUpdateTime());
//        return true;
//    }
//
//    @SneakyThrows
//    public boolean authUser(String email, String password){
//        String userHash = Hasher.hashData(email);
//        String passwordHash = Hasher.hashData(password);
//        ApiFuture<DocumentSnapshot> userQuerry = db.collection("users").document(userHash).get();
//        DocumentSnapshot userdoc = userQuerry.get();
//
//        if(!userdoc.exists()){
//            logger.info("User with email " + email + " does not exist in DB");
//            return false;
//        }
//
//        String fetchedPasswordHashed = userdoc.getString("password_hashed");
//
//        //TODO equals can return NullPointerException, should assert the fetch
//        if(fetchedPasswordHashed.equals(passwordHash)){
//            return true;
//        }
//        return false;
//    }
//
//    // public void updateUser(){
//    //     // TODO
//    // }
//
//    @SneakyThrows
//    public DocumentSnapshot getUser(String email){
//
//        String hashedEmail = Hasher.hashData(email);
//        DocumentSnapshot userDoc = db.collection("users").document(hashedEmail).get().get();
//        if(!userDoc.exists()){
//            logger.info("User " + email + " does not exist");
//            return null;
//        }
//
//        return userDoc;
//    }
//
//    @SneakyThrows
//    public void getUsers(){
//
//        // .get() initiates an async read request to the db
//        ApiFuture<QuerySnapshot> query = db.collection("users").get();
//
//        // we have another .get() here since we need to wait for the async API future read request
//        // this get() here is BLOCKING - we are waiting until db resolves
//        QuerySnapshot querySnapshot = query.get(); // QuerySnapshot = snapshot of docs in the collection
//        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
//        for (QueryDocumentSnapshot document : documents) {
//            System.out.println("User: " + document.getId());
//        }
//    }
}
