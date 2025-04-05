package com.big_hackathon.backend_v2.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import lombok.SneakyThrows;

// Should we treat the Apartment DAO as an entity for the listing themselves that may be shared amongst many people?
@Repository
public class ApartmentDAO {

    private final Firestore db;

    @Autowired
    public ApartmentDAO(Firestore db) {
        this.db = db;
    }

    public void saveListing() {
        // TODO
    }

    public void delListing() {
        // TODO
    }

    public void updateListing() {
        // TODO
    }

    @SneakyThrows
    public void getListings() {

        // TODO: Get listings should return apartment listings
        ApiFuture<QuerySnapshot> query = db.collection("apartments").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("Apartments: " + document.getId());
        }
    }

    // TODO: getListings with user as param
}
