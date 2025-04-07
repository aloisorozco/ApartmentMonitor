package com.big_hackathon.backend_v2.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import lombok.SneakyThrows;

@Repository
public class UserDAO {

    private final Firestore db;

    // No need to use Autowired - Spring injects automatically for constructors.
    public UserDAO(Firestore db) {
        this.db = db;
    }

    public void saveUser() {
        // TODO
    }

    public void delUser() {
        // TODO
    }

    public void authUser() {
        // TODO
    }

    public void updateUser() {
        // TODO
    }

    // TODO: Research and figure out how Sneaky Throws works
    @SneakyThrows
    public void getUser() {

        // TODO: Research firestore syntax + Caveats
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
        }
    }
}
