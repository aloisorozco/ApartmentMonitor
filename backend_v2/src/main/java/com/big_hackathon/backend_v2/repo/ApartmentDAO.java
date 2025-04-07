package com.big_hackathon.backend_v2.repo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.big_hackathon.backend_v2.model.Apartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import lombok.SneakyThrows;

@Repository
public class ApartmentDAO {

    private final Firestore db;
    private final Logger logger = LoggerFactory.getLogger(ApartmentDAO.class);

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

    // TODO: fetchWatchlist with user as param
    @SneakyThrows
    public List<Apartment> fetchWatchlist(String email) {
        logger.info("Fetch Watchlist user {}", email);

        String userHash = hashData(email);
        ApiFuture<QuerySnapshot> query = db.collection("users").document(userHash).collection("watchlist").get();
        QuerySnapshot snapshot = query.get();

        List<QueryDocumentSnapshot> docs = snapshot.getDocuments();
        List<Apartment> apartments = new ArrayList<>();
        for (QueryDocumentSnapshot document : docs) {
            String location = document.getString("location");
            String imageLink = document.getString("image_link");
            String url = document.getString("url");
            String description = document.getString("description");
            double price = document.getDouble("price") != null ? document.getDouble("price") : 0.0;
            double price_target = document.getDouble("price_target") != null ? document.getDouble("price_target") : 0.0;

            Apartment apartment = Apartment.builder()
                    .location(location)
                    .imageLink(imageLink)
                    .url(url)
                    .description(description)
                    .price(price)
                    .priceTarget(price_target)
                    .build();
            apartments.add(apartment);
        }

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
