package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.service.ApartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/")
    public String listApartments() {
        logger.info("listApartments endpoint called");
        return apartmentService.listApartments();
    }

    @GetMapping("/{id}")
    public String getApartment(@PathVariable Long id) {
        logger.info("getApartment endpoint called");
        return apartmentService.getApartment(id);
    }

    @PostMapping("/")
    public String insertApartment(@RequestBody Apartment apartment) {
        logger.info("insertApartment endpoint called");
        return apartmentService.insertApartment(apartment);
    }

    @PutMapping("/{id}")
    public String updateApartment(@RequestBody Apartment apartment, @PathVariable Long id) {
        logger.info("updateApartment endpoint called");
        return apartmentService.updateApartment(apartment, id);
    }

    @DeleteMapping("/{id}")
    public String deleteApartment(@PathVariable Long id) {
        logger.info("deleteApartment endpoint called");
        return apartmentService.deleteApartment(id);
    }

}
