package com.big_hackathon.backend_v2.repo;

import java.util.ArrayList;
import java.util.List;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.Hasher;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;

import lombok.SneakyThrows;

@Repository
public class ApartmentDAO {

    private final Firestore db;
    private final Logger logger = LoggerFactory.getLogger(ApartmentDAO.class);

    @Autowired
    public ApartmentDAO(Firestore db) {
        this.db = db;
    }

    @SneakyThrows
    public String insertApartment(Apartment apartment, String email) {
        //TODO fix the logic, should take into account the user email
        DocumentSnapshot lookupApartment = db.collection("users").document(email).collection("watchlist").document(apartment.getListingID()).get().get();
        if(lookupApartment.exists()){
            //TODO error middleware could throw an exception
            logger.info("Apartment already exists");
            return "FAIL";
        }

        WriteResult result = db.collection("users").document(email).collection("watchlist").document(apartment.getListingID()).set(apartment).get();
        logger.info("Created new apartment {} at time {}", apartment.getListingID(), result.getUpdateTime());

        return "SUCCESS";
    }

    @SneakyThrows
    public String deleteApartment(String email, String listingId) {
        ApiFuture<WriteResult> res = db.collection("users").document(Hasher.hashData(email)).collection("watchlist").document(listingId).delete();

        // TODO: this will throw an error in the event the .get() fails - we should handle this in error middleware
        // TODO: so no SneakyThrows?
        WriteResult metadata = res.get();
        logger.info("Deleted apartment {} at time {}", listingId, metadata.getUpdateTime());
        return "SUCCESS";
    }

    //QUESTION should the param just be an object? Or ID, arraylist of values
    public void updateApartment() {
        // TODO
    }

    @SneakyThrows
    public List<Apartment> fetchWatchlist(String email) {
        String userHash = Hasher.hashData(email);
        //FIXME talk to Daniel if futureapi is needed (this way done here will block the thread)? With futureapi we can set a async call
        QuerySnapshot snapshot = db.collection("users").document(userHash).collection("watchlist").get().get();

        List<QueryDocumentSnapshot> docs = snapshot.getDocuments();
        List<Apartment> apartments = new ArrayList<>();
        for (QueryDocumentSnapshot document : docs) {
            //TODO since we pass spring objects to our databse, the key value pairs match those
            String listingID = document.getString("listingID");
            String location = document.getString("location");
            String imageLink = document.getString("imageLink");
            String url = document.getString("url");
            String description = document.getString("description");
            double price = document.getDouble("price") != null ? document.getDouble("price") : 0.0;

            Apartment apartment = Apartment.builder()
                    .listingID(listingID)
                    .location(location)
                    .imageLink(imageLink)
                    .url(url)
                    .description(description)
                    .price(price)
                    .build();
            apartments.add(apartment);
        }

        logger.info("Apartment fetchWatchlist for user {} called at time {}", email, System.currentTimeMillis() / 1000L);

        // Return the list of ApartmentDTOs
        return apartments;
    }
}
