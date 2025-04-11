package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String email) {
        logger.info("getUser endpoint called");
        return userService.getUser(email);
    }

    @PostMapping("/")
    public String insertUser(@RequestBody String email, @RequestBody String password, @RequestBody String fname, @RequestBody String lname) {
        logger.info("insertUser endpoint called");
        return userService.saveUser(email, password, fname, lname);
    }

    // @PutMapping("/{id}")
    // public String updateUser(@PathVariable Long id) {
    //     logger.info("updateUser endpoint called");
    //     return userService.updateUser(id);
    // }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable String id) {
        logger.info("deleteUser endpoint called");
        return userService.deleteUser(id);
    }

    @DeleteMapping("/")
    public String authUser(@RequestBody String email, @RequestBody String password) {
        logger.info("deleteUser endpoint called");
        return userService.authUser(email, password);
    }

}
