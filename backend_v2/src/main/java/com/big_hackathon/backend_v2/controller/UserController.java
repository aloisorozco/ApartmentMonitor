package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserService userService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String listUsers() {
        logger.info("listUsers endpoint called");
        return userService.listUsers();
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        logger.info("getUser endpoint called");
        return userService.getUser(id);
    }

    @PostMapping("/")
    public String insertUser(@RequestBody User user) {
        logger.info("insertUser endpoint called");
        return userService.insertUser(user);
    }

    @PutMapping("/{id}")
    public String updateUser(@RequestBody User user, @PathVariable Long id) {
        logger.info("updateUser endpoint called");
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        logger.info("deleteUser endpoint called");
        return userService.deleteUser(id);
    }

}
