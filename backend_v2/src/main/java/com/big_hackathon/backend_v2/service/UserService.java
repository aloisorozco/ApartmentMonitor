package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.UserDAO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder){
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
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
                .apartments(new ArrayList<Apartment>())
                .build());

        return u;
    }

    // public String updateUser(Long id) {
    //     return "TODO - Set up DB Access first";
    // }

    public void deleteUser(String email) {
        userDAO.deleteById(userDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserID());
    }
}
