package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.repository.ApartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentService {

    private ApartmentRepository apartmentRepo;

    public List<Apartment> listApartments() {
        return apartmentRepo.findAll();
    }

    public Apartment getApartment(Long id) {
        return apartmentRepo.getById(id);
    }

    public Apartment insertApartment(Apartment apartment) {
        return apartmentRepo.save(apartment);
    }

    public Apartment updateApartment(Apartment apartment) {
        return apartmentRepo.save(apartment);
    }

    public void deleteApartment(Long id) {
        apartmentRepo.deleteById(id);
    }
}
