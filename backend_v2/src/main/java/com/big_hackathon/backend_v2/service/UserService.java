package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Hasher;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.UserDAO;
import com.google.cloud.firestore.DocumentSnapshot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public User getUser(String email) {

        DocumentSnapshot userDoc = userDAO.getUser(email);

        //TODO potential error, variables return null, we need to check for that
        String fname = userDoc.getString("fname");
        String lname = userDoc.getString("lname");
        String password = userDoc.getString("password_hashed");
        long createdAt = userDoc.getLong("createdAt");

        return User.builder().firstName(fname).lastName(lname).email(email).passwordHashed(password).createdAt(createdAt).build();
    }

    public String saveUser(String email, String password, String fname, String lname) {

        long createAt = System.currentTimeMillis() / 1000L;
        String passwordHash = Hasher.hashData(password);
        User newUser = User.builder().email(email).passwordHashed(passwordHash).firstName(fname).lastName(lname).createdAt(createAt).build();

        return (userDAO.saveUser(newUser) ? "SUCCESS" : "FAIL");
    }

    // public String updateUser(Long id) {
    //     return "TODO - Set up DB Access first";
    // }

    public String deleteUser(String id) {
        return (userDAO.delUser(id) ? "SUCCESS" : "FAIL");
    }

    public String authUser(String email, String password) {
        return (userDAO.authUser(email, password) ? "SUCCESS" : "FAIL");
    }
}
