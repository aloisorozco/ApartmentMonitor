package com.big_hackathon.backend_v2.repo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.big_hackathon.backend_v2.model.Apartment;
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
    public boolean saveApartment(Apartment apartment) {
        DocumentSnapshot lookupApartment = db.collection("apartments").document(apartment.getListingID()).get().get();
        if(lookupApartment.exists()){
            //TODO error middleware could throw an exception
            logger.info("Apartment already exists");
            return false;
        }

        //QUESTION we don't need to pass it as an arraylist?
        WriteResult result = db.collection("apartments").document(apartment.getListingID()).set(apartment).get();
        logger.info("Created new apartment {} at time {}", apartment.getListingID(), result.getUpdateTime());

        return true;
    }

    @SneakyThrows
    public boolean deleteApartment(Apartment apartment) {
        ApiFuture<WriteResult> res = db.collection("apartments").document(apartment.getListingID()).delete();

        // TODO: this will throw an error in the event the .get() fails - we should handle this in error middleware
        WriteResult metadata = res.get();
        logger.info("Deleted apartment {} at time {}", apartment.getListingID(), metadata.getUpdateTime());
        return true;
    }

    //QUESTION should the param just be an object? Or ID, arraylist of values
    public void updateApartment() {
        // TODO
    }

    @SneakyThrows
    public List<Apartment> fetchWatchlist(String email) {
        String userHash = hashData(email);
        //FIXME talk to Daniel if futureapi is needed (this way done here will block the thread)? With futureapi we can set a async call
        QuerySnapshot snapshot = db.collection("users").document(userHash).collection("watchlist").get().get();;

        List<QueryDocumentSnapshot> docs = snapshot.getDocuments();
        List<Apartment> apartments = new ArrayList<>();
        for (QueryDocumentSnapshot document : docs) {
            String listingID = document.getString("listing_id");
            String location = document.getString("location");
            String imageLink = document.getString("image_link");
            String url = document.getString("url");
            String description = document.getString("description");
            double price = document.getDouble("price") != null ? document.getDouble("price") : 0.0;
            double price_target = document.getDouble("price_target") != null ? document.getDouble("price_target") : 0.0;

            Apartment apartment = Apartment.builder()
                    .listingID(listingID)
                    .location(location)
                    .imageLink(imageLink)
                    .url(url)
                    .description(description)
                    .price(price)
                    .priceTarget(price_target)
                    .build();
            apartments.add(apartment);
        }

        logger.info("Apartment fetchWatchlist for user {} called at time {}", email, System.currentTimeMillis() / 1000L);

        // Return the list of ApartmentDTOs
        return apartments;
    }


    //TODO should be removed when "hasher" bean is created
    @SneakyThrows
    public static String hashData(String data){
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return hashByte2Hex(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
    }

    //TODO should be removed when "hasher" bean is created
    private static String hashByte2Hex(byte[] hash){

        // size of 2 * len of hash since every byte becomes a two char hex value
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0'); // ex: hex = a, we add 0 in front to keep the hex num two char long
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
