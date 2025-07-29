package com.big_hackathon.backend_v2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.big_hackathon.backend_v2.model.SpringSUser;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.UserRepo;

public class AuthUserService implements UserDetailsService{

    private final UserRepo userRepo;

    public AuthUserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    // TODO: ask nico to make a username field in the db -> username is just a unique key other than the ID.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + ", not found"));

        return new SpringSUser(user);
    }
    
}
