package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.service.ApartmentService;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;
    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    @Autowired
    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/")
    public List<Apartment> getUserWatchlist() {
        logger.info("listApartments endpoint called");
        return apartmentService.getWatchlist();
    }

    @GetMapping("/{id}")
    public String getApartment(@PathVariable Long id) {
        logger.info("getApartment endpoint called");
        return apartmentService.getApartment(id);
    }

    @PostMapping("/insert")
    public String insertApartment(@RequestBody Apartment apartment) {
        logger.info("insertApartment endpoint called");
        return apartmentService.insertApartment(apartment);
    }

    //FIXME figure out use
//    @PutMapping("/{id}")
//    public String updateApartment(@RequestBody Apartment apartment, @PathVariable Long id) {
//        logger.info("updateApartment endpoint called");
//        return apartmentService.updateApartment(apartment, id);
//    }

    @DeleteMapping("/{id}")
    public String deleteApartment(@PathVariable Long id) {
        logger.info("deleteApartment endpoint called");
        return apartmentService.deleteApartment(id);
    }

}
