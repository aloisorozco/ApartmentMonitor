package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Apartment;
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

    public List<Apartment> listApartments() {
        return apartmentDAO.fetchWatchlist("grimut.daniel@gmail.com");
    }

    public String getApartment(Long id) {
        return "TODO - Set up DB Access first";
    }

    public String insertApartment(Apartment apartment) {
        return "TODO - Set up DB Access first";
    }

    public String updateApartment(Apartment apartment, Long id) {
        return "TODO - Set up DB Access first";
    }

    public String deleteApartment(Long id) {
        return "TODO - Set up DB Access first";
    }
}
