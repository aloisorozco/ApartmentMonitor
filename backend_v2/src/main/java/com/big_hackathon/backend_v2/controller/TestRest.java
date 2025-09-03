package com.big_hackathon.backend_v2.controller;

import org.springframework.web.bind.annotation.RestController;

import com.big_hackathon.backend_v2.filter.RateLimited;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestRest {
    
    @GetMapping("/hello")
    public ResponseEntity<String> hello(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("hello vanilla called (no rate limit): " + userDetails.getUsername());
    }

    @RateLimited
    @GetMapping("/hello_ratelimitted")
    public ResponseEntity<String> helloRl(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("hello RATE LIMITTED called: " + userDetails.getUsername());
    }
    
}
