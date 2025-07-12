package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.DTO.ApartmentDTO;
import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.ApartmentDAO;
import com.big_hackathon.backend_v2.repo.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {

    private final ApartmentDAO apartmentDAO;
    private final UserDAO userDAO;

    @Autowired
    public ApartmentService(ApartmentDAO apartmentDAO, UserDAO userDAO) {
        this.apartmentDAO = apartmentDAO;
        this.userDAO = userDAO;
    }

    public List<ApartmentDTO> getWatchlist(String email) {
        Optional<User> user = userDAO.findByEmail(email);
        if(user.isPresent()){
            //retrieve the user since it exists
            User u = user.get();
            //return users apartment list as a DTO list
            return u.getApartments().stream().map(ApartmentDTO::new).toList();
        }
        throw new RuntimeException("User not found");
    }

    public String getApartment(Long id) {
        return "TODO - Set up DB Access first";
    }

    public ApartmentDTO insertApartment(String email, String url) {
        Optional<User> user = userDAO.findByEmail(email);
        //TODO check if exists in user watchlist, also check if already exists in db and return that one
        if(user.isPresent()){
            User u = user.get();

            boolean exists = u.getApartments().stream().anyMatch(a -> a.getUrl().equals(url));
            if(exists){throw new RuntimeException("Apartment already exists");}

            Apartment apartment = JavaWebScraper.scrapeKijiji(url);

            u.getApartments().add(apartment);
            userDAO.save(u);
            return apartment.useDTO();
        }
        throw new RuntimeException("User not found");
    }

    //TODO check to see if many to many makes this challenging
    public String updateApartment(ApartmentDTO apartment, Long id) {
        return "TODO - Set up DB Access first";
    }

    public void deleteApartment(String email, Long listingId) {
        Optional<Apartment> apartment = apartmentDAO.findById(listingId);
        Optional<User> user = userDAO.findByEmail(email);
        if(user.isPresent() && apartment.isPresent()){
            Apartment a = apartment.get();
            User u = user.get();
            u.getApartments().remove(a);
            userDAO.save(u);
        }

    }
}
