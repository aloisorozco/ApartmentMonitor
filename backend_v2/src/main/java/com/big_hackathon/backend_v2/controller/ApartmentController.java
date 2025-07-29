package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.DTO.ApartmentDTO;
import com.big_hackathon.backend_v2.service.ApartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // //MOVED TO USER
    // @GetMapping("/fetch_watchlist")
    // public ResponseEntity<?> getUserWatchlist(@RequestParam  String email) {
    //     logger.info("getUserWatchlist endpoint called");
    //     return ResponseEntity.status(HttpStatus.OK).body(apartmentService.getWatchlist(email));
    // }

//    @GetMapping("/{id}")
//    public String getApartment(@PathVariable Long id) {
//        logger.info("getApartment endpoint called");
//        return apartmentService.getApartment(id);
//    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertApartment(@RequestBody Map<String, String> json) {
        logger.info("insertApartment endpoint called");
        try{
            
            apartmentService.insertApartment(Double.valueOf(json.get("price")), json.get("location"), json.get("description"), json.get("image_link"), json.get("url"));
            return new ResponseEntity<>("Apartment inserted successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error inserting apartment: {}", e.getMessage());
            return new ResponseEntity<>("Adding apartment failed", HttpStatus.BAD_REQUEST);
        }
    }

    //FIXME figure out use
//    @PutMapping("/{id}")
//    public String updateApartment(@RequestBody Apartment apartment, @PathVariable Long id) {
//        logger.info("updateApartment endpoint called");
//        return apartmentService.updateApartment(apartment, id);
//    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteApartment(@RequestParam Long listingId) {
        logger.info("deleteApartment endpoint called");
        try{
            apartmentService.deleteApartment(listingId);
            return new ResponseEntity<>("Deleting apartment successful", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting apartment: {}", e.getMessage());
            return new ResponseEntity<>("Deleting apartment failed", HttpStatus.BAD_REQUEST);
        }
    }

}
