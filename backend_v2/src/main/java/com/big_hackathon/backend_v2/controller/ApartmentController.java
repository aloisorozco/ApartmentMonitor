package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.service.ApartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

    @Autowired
    private final ApartmentService apartmentService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/")
    public List<Apartment> listApartments() {
        logger.info("listApartments endpoint called");
        return apartmentService.listApartments();
    }

    @GetMapping("/{id}")
    public Apartment getApartment(@PathVariable Long id) {
        logger.info("getApartment endpoint called");
        return apartmentService.getApartment(id);
    }

    @PostMapping("/")
    public Apartment insertApartment(@RequestBody Apartment apartment) {
        logger.info("insertApartment endpoint called");
        return apartmentService.insertApartment(apartment);
    }

    @PutMapping("/")
    public Apartment updateApartment(@RequestBody Apartment apartment) {
        logger.info("updateApartment endpoint called");
        return apartmentService.updateApartment(apartment);
    }

    @DeleteMapping("/{id}")
    public String deleteApartment(@PathVariable Long id) {
        logger.info("deleteApartment endpoint called");
        apartmentService.deleteApartment(id);
        return "OK";
    }

}
