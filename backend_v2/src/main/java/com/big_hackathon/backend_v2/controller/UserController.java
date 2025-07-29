package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import com.big_hackathon.backend_v2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public UserController(UserService userService) {
        this.userService = userService;
        System.out.println("TEST ENDPOINT HIT!");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        logger.info("getUser endpoint called");
        try{
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(email));
        } catch (Exception e) {
            logger.error("Error retrieving user: {}", e.getMessage());
            return new ResponseEntity<>("Retrieving user failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> insertUser(@RequestBody Map<String, String> json) {
        logger.info("Register user endpoint called");

        String email = json.get("email");
        String password = json.get("password");
        String fname = json.get("fname");
        String lname = json.get("lname");

        try {
            userService.saveUser(email, password, fname, lname);
            return new ResponseEntity<>("Registration successful", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, String> json) {
        logger.info("Update user endpoint called");

        String email = json.get("email");
        String password = json.get("password");
        String fname = json.get("fname");
        String lname = json.get("lname");

        try {
            userService.updateUser(email, password, fname, lname);
            return new ResponseEntity<>("Update successful", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return new ResponseEntity<>("Update failed", HttpStatus.BAD_REQUEST);
        }
    }

    // @PutMapping("/{id}")
    // public String updateUser(@PathVariable Long id) {
    //     logger.info("updateUser endpoint called");
    //     return userService.updateUser(id);
    // }

    //TODO add user and admin roles. And map delete to @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String email) {
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

    @GetMapping("/fetch_watchlist")
    public ResponseEntity<?> getUserWatchlist(@RequestParam  String email) {
        logger.info("getUserWatchlist endpoint called");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getWatchlist(email));
    }

}
