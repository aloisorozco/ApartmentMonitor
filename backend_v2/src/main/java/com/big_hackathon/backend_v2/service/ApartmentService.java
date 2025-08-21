package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.DTO.ApartmentDTO;
import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.ApartmentRepo;
import com.big_hackathon.backend_v2.repo.UserRepo;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ApartmentService {

    private final ApartmentRepo apartmentREPO;
    private final UserRepo userREPO;
    
    public ApartmentService(ApartmentRepo apartmentREPO, UserRepo userREPO) {
        this.apartmentREPO = apartmentREPO;
        this.userREPO = userREPO;
        
    }

    // public List<ApartmentDTO> getWatchlist(String email) {
    //     Optional<User> user = userDAO.findByEmail(email);
    //     if(user.isPresent()){
    //         //retrieve the user since it exists
    //         User u = user.get();
    //         //return users apartment list as a DTO list
    //         return u.getApartments().stream().map(ApartmentDTO::new).toList();
    //     }
    //     throw new RuntimeException("User not found");
    // }

    public ApartmentDTO getApartment(Long id) {
        return this.apartmentREPO.findByListingID(id)
                .map(ApartmentDTO::new)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));
    }

    public void insertApartment(double price, String location, String description, String imagelink, String url){
        this.apartmentREPO.save(Apartment
                .builder()
                .price(price)
                .location(location)
                .description(description)
                .imageLink(imagelink)
                .url(url)
                .build());
    }

    //TODO Test to see if works. unsure of best implementation
    public void updateApartment(Apartment apartment) {
        this.apartmentREPO.save(apartment);
    }

    public void deleteApartment(long listingID) {
        this.apartmentREPO.deleteById(apartmentREPO.findByListingID(listingID)
                .orElseThrow(() -> new RuntimeException("Apartment not found"))
                .getListingID());
    }


    public ApartmentDTO insertApartment(String email, String url) {
        Optional<User> user = userREPO.findByEmail(email);
        //TODO check if exists in user watchlist, also check if already exists in db and return that one
        if(user.isPresent()){
            User u = user.get();

            boolean exists = u.getApartments().stream().anyMatch(a -> a.getUrl().equals(url));
            if(exists){throw new RuntimeException("Apartment already exists");}

            Apartment apartment = JavaWebScraper.scrapeKijiji(url);

            u.getApartments().add(apartment);
            userREPO.save(u);
            return apartment.useDTO();
        }
        throw new RuntimeException("User not found");
    }



    // public void deleteApartment(String email, Long listingId) {
    //     Optional<Apartment> apartment = apartmentREPO.findById(listingId);
    //     Optional<User> user = userDAO.findByEmail(email);
    //     if(user.isPresent() && apartment.isPresent()){
    //         Apartment a = apartment.get();
    //         User u = user.get();
    //         u.getApartments().remove(a);
    //         userDAO.save(u);
    //     }

    // }
}
