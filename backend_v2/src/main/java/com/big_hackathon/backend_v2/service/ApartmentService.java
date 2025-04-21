package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.Hasher;
import com.big_hackathon.backend_v2.repo.ApartmentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApartmentService {

    private final ApartmentDAO apartmentDAO;

    @Autowired
    public ApartmentService(ApartmentDAO apartmentDAO) {
        this.apartmentDAO = apartmentDAO;
    }

    public List<Apartment> getWatchlist(String email) {
        return apartmentDAO.fetchWatchlist(email);
    }

    public String getApartment(Long id) {
        return "TODO - Set up DB Access first";
    }

    public Apartment insertApartment(String email, String url) {
        Apartment apartment = JavaWebScraper.scrapeKijiji(url);
        //TODO need help figuring out logic: What should we return when apartment already exists/how to check already exists?
        if(apartmentDAO.insertApartment(apartment, Hasher.hashData(email)).equals("SUCCESS")){
            return apartment;
        } else {
            return null;
        }
    }

    public String updateApartment(Apartment apartment, Long id) {
        return "TODO - Set up DB Access first";
    }

    public String deleteApartment(String email, String listingId) {
        return apartmentDAO.deleteApartment(email, listingId);
    }
}
