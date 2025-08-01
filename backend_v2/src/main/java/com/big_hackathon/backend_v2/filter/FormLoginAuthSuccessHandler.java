package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
            Map<String, String> payload = new HashMap<>();

            payload.put("sub", user.getUserID());  
            payload.put("email", user.getUsername()); 
            payload.put("name", user.getFName() + " " + user.getLName()); 
            String jwt = jwtUtil.generateJWT(payload);
            String refreshToken = jwtUtil.generateRefreshToken();

            String hashed = encoder.encode(refreshToken);
        
            // Assuming the refresh token for that user is deleted when user loggs out.
            refreshTokenDAO.save(RefreshToken.builder().userID(null).tokenValue(hashed).build());

            // we set the refresh token as a cookie to prevent XSS attacks - it is risky for us to send the refresh token in plain text, and save in user session!
            // if someone gets hold of the refresh token and user email, they pretty much can request a new JWT, which mean nico gets fired from the company for slacking!
            ResponseCookie refreshCookie  = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) // assures that the cookie is accesed only by HTTP - cannot do smt like document.getCookie in JS!
                .secure(false) // TODO: when we have HTTPS setup, switch back to 'true' to ensure that cookie only send over HTTPS
                .sameSite("None") // TODO: also change to "Strict" later on, to prevent cookie being sent in CSRF situations.
                .path("/auth/refresh_token") // VERY IMPORTANT! this tells the browser "attach the refresh token when requesting to this endpoint"
                .maxAge(Duration.ofDays(3))
                .build();

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
