package com.big_hackathon.backend_v2.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authorities implements GrantedAuthority{
    ADMIN, USER, POSTER;

    @Override
    public String getAuthority() {
        return name();
    }
    
}
