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
public class FormLoginAuthSuccessHandler implements AuthenticationSuccessHandler{

    private final JwtUtil jwtUtil;

    FormLoginAuthSuccessHandler(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // creating and sending out the JWT at the end of form based loggin.
        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails){
            SpringSUser user = (SpringSUser) principal;
            Map<String, String> payload = new HashMap<>();

            payload.put("sub", user.getUserID());  
            payload.put("email", user.getUsername()); 
            payload.put("name", user.getFName() + " " + user.getLName()); 
            String jwt = jwtUtil.generateJWT(payload);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + jwt + "\"}");
            response.flushBuffer();

        }else{
            throw new ServletException("Issues with Form Login JWT generation");
        }

        
    }
    
}
