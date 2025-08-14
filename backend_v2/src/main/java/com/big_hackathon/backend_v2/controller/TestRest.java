package com.big_hackathon.backend_v2.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestRest {
    
    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal UserDetails userDetails) {
        return "This works because daniel is carrying this whole project: " + userDetails.getUsername();
    }
    
}
