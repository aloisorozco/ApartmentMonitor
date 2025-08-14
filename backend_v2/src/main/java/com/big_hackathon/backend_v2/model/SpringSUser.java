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

    // Spring will use this method to compare passowrds
    // TODO: keep plain text for testing, but should we encrypt stuff on the DB side, we would need to overwrite the PasswordEncoder Bean with a custom passowrd matching implementation.
    @Override
    public String getPassword() {
        return this.user.getHashedPassword().toString();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail().toString();
        
    }

    public String getFName() {
        return this.user.getFirstName().toString();
    }

    public String getLName() {
        return this.user.getLastName().toString();
    }

    public User getUser() {
        return this.user;
    }
    
}
