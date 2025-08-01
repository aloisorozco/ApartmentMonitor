package com.big_hackathon.backend_v2.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.big_hackathon.backend_v2.filter.JwtUtil;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register_user")
    public ResponseEntity<String> insertUser(@RequestBody Map<String, String> json) {
        logger.info("Register user endpoint called");

        String email = json.get("email");
        String password = json.get("password");
        String fname = json.get("fname");
        String lname = json.get("lname");

        try {
            User user = userService.saveUser(email, password, fname, lname);
            Map<String, String> payload = new HashMap<>();

            payload.put("sub", user.getUserID().toString());  
            payload.put("email", email); 
            payload.put("name", fname + " " + lname); 
            String jwt = jwtUtil.generateJWT(payload);

            // Adding the JWT into the header of the response on sucessfull registration, like that user can start making API requests.
            return ResponseEntity.status(HttpStatus.CREATED)
                .header("Authorization", "Bearer " + jwt)
                .body("User registered successfully");

        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/refresh_token")
    public ResponseEntity<String> refreshToken(@CookieValue("refreshToken") String refreshToken, @RequestBody Map<String, String> payload) {
        // TODO: check if user exists in DBby email, fetch user_id and check if user_id is assoiated with a refresh token in DB, hash the recieved token and match it with token in DB,
        // generate new token and replace old token, send back the new token as cookie. 
        return null;
    }
}
