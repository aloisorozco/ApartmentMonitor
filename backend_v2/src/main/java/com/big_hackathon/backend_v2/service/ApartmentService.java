package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentService {

    @Autowired
    private ApartmentRepository apartmentRepo;

    public List<Apartment> listApartments() {
        return apartmentRepo.findAll();
    }

    public Apartment getApartment(String id) {
        return apartmentRepo.getById(id);
    }

    public Apartment insertApartment(Apartment apartment) {
        return apartmentRepo.save(apartment);
    }

    public Apartment updateApartment(Apartment apartment) {
        return apartmentRepo.save(apartment);
    }

    public void deleteApartment(String id) {
        apartmentRepo.deleteById(id);
    }
}
