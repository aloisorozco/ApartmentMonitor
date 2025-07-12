package com.big_hackathon.backend_v2.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSUser implements UserDetails{

    private final User user;

    public SpringSUser(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.user.getAuthority());
    }

    @Override
    public String getPassword() {
        return this.user.getHashedPassword().toString();
    }

    @Override
    public String getUsername() {
        return this.user.getUserID().toString();
    }
    
}
