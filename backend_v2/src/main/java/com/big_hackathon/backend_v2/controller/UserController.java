package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.filter.JwtUtil;
import com.big_hackathon.backend_v2.model.SpringSUser;
import com.big_hackathon.backend_v2.service.UserService;
import com.big_hackathon.backend_v2.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final JwtUtil jwtUtil;

    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        logger.info("getUser endpoint called");
        try{
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(email));
        } catch (Exception e) {
            logger.error("Error retrieving user: {}", e.getMessage());
            return new ResponseEntity<>("Retrieving user failed", HttpStatus.BAD_REQUEST);
        }
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

    // @PutMapping("/{id}")
    // public String updateUser(@PathVariable Long id) {
    //     logger.info("updateUser endpoint called");
    //     return userService.updateUser(id);
    // }

    //TODO add user and admin roles. And map delete to @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody String email) {
        logger.info("deleteUser endpoint called");
        try{
            userService.deleteUser(email);
            return new ResponseEntity<>("Delete user successful", HttpStatus.OK);
        } catch (Exception e){
            logger.error("Error deleting user: {}", e.getMessage());
            return new ResponseEntity<>("Delete user failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/auth_user")
    public ResponseEntity<String> authUser(@RequestBody Map<String, String> json) {
        logger.info("Authenticate user endpoint called");
        return new ResponseEntity<>("Delete user successful", HttpStatus.OK);

        //TODO will be changed with OAuth and JWT
    }

}
