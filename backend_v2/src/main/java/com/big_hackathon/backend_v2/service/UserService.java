package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import com.big_hackathon.backend_v2.DTO.ApartmentDTO;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userDAO;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userDAO, PasswordEncoder passwordEncoder){
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean exists(String email) {
        return userDAO.existsByEmail(email);
    }

    public UserDTO getUser(String email) {

        return userDAO.findByEmail(email)
                .map(UserDTO::new)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User saveUser(String email, String password, String fname, String lname) {
        User u = userDAO.save(User
                .builder()
                .email(email)
                .hashedPassword(passwordEncoder.encode(password))
                .firstName(fname)
                .lastName(lname)
                .build());

        return u;
    }

    //TODO Test to see if works
    public void updateUser(String email, String password, String fname, String lname) {
        User user = userDAO.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(fname);
        user.setLastName(lname);
        user.setHashedPassword(passwordEncoder.encode(password));

        userDAO.save(user);
    }

    public void deleteUser(String email) {
        userDAO.deleteById(userDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getEmail());
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

    public String authUser(String email, String password) {
//        return (userREPO.authUser(email, password) ? "SUCCESS" : "FAIL");
        return null;
    }
}
