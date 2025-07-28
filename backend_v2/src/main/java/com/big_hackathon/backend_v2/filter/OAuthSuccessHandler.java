package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.model.SpringSUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler{

    private final JwtUtil jwtUtil;

    OAuthSuccessHandler(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // creating and sending out the JWT at the end of form based loggin.
        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails){


        }else{
            throw new ServletException("Issues with Form Login JWT generation");
        }
    }

    
}
