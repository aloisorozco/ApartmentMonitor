package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.service.ApartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;
    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    @Autowired
    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/fetch_watchlist")
    public List<Apartment> getUserWatchlist(@RequestParam  String email) {
        logger.info("getUserWatchlist endpoint called");
        return apartmentService.getWatchlist(email);
    }

    @GetMapping("/{id}")
    public String getApartment(@PathVariable Long id) {
        logger.info("getApartment endpoint called");
        return apartmentService.getApartment(id);
    }

    @PostMapping("/insert")
    public Apartment insertApartment(@RequestBody Map<String, String> json) {
        logger.info("insertApartment endpoint called");
        return apartmentService.insertApartment(json.get("email"), json.get("url"));
    }

    //FIXME figure out use
//    @PutMapping("/{id}")
//    public String updateApartment(@RequestBody Apartment apartment, @PathVariable Long id) {
//        logger.info("updateApartment endpoint called");
//        return apartmentService.updateApartment(apartment, id);
//    }

    @DeleteMapping("/delete_apartment")
    public String deleteApartment(@RequestBody Map<String, String> json) {
        logger.info("deleteApartment endpoint called");
        return apartmentService.deleteApartment(json.get("email"), json.get("listingId"));
    }

}
