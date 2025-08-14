package com.big_hackathon.backend_v2.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.SpringSUser;
import com.big_hackathon.backend_v2.repo.RefreshTokenDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FormLoginAuthSuccessHandler implements AuthenticationSuccessHandler{

    private final JwtUtil jwtUtil;
    private final RefreshTokenDAO refreshTokenDAO;

    FormLoginAuthSuccessHandler(JwtUtil jwtUtil, RefreshTokenDAO refreshTokenDAO){
        this.jwtUtil = jwtUtil;
        this.refreshTokenDAO = refreshTokenDAO;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // creating and sending out the JWT at the end of form based loggin.
        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails){
            SpringSUser user = (SpringSUser) principal;
            String jwt = jwtUtil.generateJWT(user.getUsername(), user.getFName() + " " + user.getLName());
            ResponseCookie refreshCookie = jwtUtil.generateRefreshTokenAsCookie();
            
            // Issue new refresh token on every login, and set it as active.
            refreshTokenDAO.save(RefreshToken.builder().user(user.getUser()).tokenValue(refreshCookie.getValue()).isActive(true).build());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());    

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + jwt + "\"}");
            response.flushBuffer();

        }else{
            throw new ServletException("Issues with Form Login JWT generation");
        }

        
    }
    
}
