package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.InsertWatchlistTemplate;
import com.big_hackathon.backend_v2.repo.ApartmentDAO;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public String insertApartment(InsertWatchlistTemplate templateApartment) {
        //return apartmentDAO.saveApartment(apartment);
        //TODO needs to be scrape the data. Need to create a listingID
        return null;
    }

    public String updateApartment(Apartment apartment, Long id) {
        return "TODO - Set up DB Access first";
    }

    public String deleteApartment(Long id) {
        return "TODO - Set up DB Access first";
    }
}
