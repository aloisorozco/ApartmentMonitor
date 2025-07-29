package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import com.big_hackathon.backend_v2.DTO.ApartmentDTO;
import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.Hasher;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userREPO;

    @Autowired
    public UserService(UserRepo userREPO){
        this.userREPO = userREPO;
    }

    public UserDTO getUser(String email) {

        return userREPO.findByEmail(email)
                .map(UserDTO::new)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void saveUser(String email, String password, String fname, String lname) {
        userREPO.save(User
                .builder()
                .email(email)
                .hashedPassword(Hasher.hashData(password))
                .firstName(fname)
                .lastName(lname)
                .build());
    }

    //TODO Test to see if works
    public void updateUser(String email, String password, String fname, String lname) {
        User user = userREPO.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(fname);
        user.setLastName(lname);
        user.setHashedPassword(Hasher.hashData(password));

        userREPO.save(user);
    }

    public void deleteUser(String email) {
        userREPO.deleteById(userREPO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserID());
    }

    public List<ApartmentDTO> getWatchlist(String email) {
        Optional<User> user = userREPO.findByEmail(email);
        if(user.isPresent()){
            //retrieve the user since it exists
            User u = user.get();
            //return users apartment list as a DTO list
            return u.getApartments().stream().map(ApartmentDTO::new).toList();
        }
        throw new RuntimeException("User not found");
    }

    public String authUser(String email, String password) {
//        return (userREPO.authUser(email, password) ? "SUCCESS" : "FAIL");
        return null;
    }
}
