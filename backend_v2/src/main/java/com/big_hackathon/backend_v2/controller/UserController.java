package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable String id) {
        logger.info("getUser endpoint called");
        return userService.getUser(id);
    }

    @PostMapping("/insert")
    public String insertUser(@RequestBody String email, @RequestBody String password, @RequestBody String fname, @RequestBody String lname) {
        logger.info("insertUser endpoint called");
        return userService.saveUser(email, email, password, fname, lname);
    }

    // @PutMapping("/{id}")
    // public String updateUser(@PathVariable Long id) {
    //     logger.info("updateUser endpoint called");
    //     return userService.updateUser(id);
    // }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        logger.info("deleteUser endpoint called");
        return userService.deleteUser(id);
    }

    @PostMapping("/auth_user")
    public ResponseEntity<String> authUser(@RequestBody Map<String, String> json) {
        logger.info("Authenticate user endpoint called");

        String email = json.get("email");
        String password = json.get("password");
        System.out.println(email + " and " + password);
        String result = userService.authUser(email, password);

        if(Objects.equals(result, "SUCCESS")){
            System.out.println("SUCCESSFUL LOGIN");
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            System.out.println("FAILED LOGIN");
            return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
        }
    }

}
