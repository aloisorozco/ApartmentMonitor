package com.big_hackathon.backend_v2.controller.refreshers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.controller.ApartmentController;
import com.big_hackathon.backend_v2.filter.JwtUtil;
import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.service.AuthUserService;

@Component
public class NativeTokenRefreser implements IJwtRefresher{

    private final JwtUtil jwtUtil;

    private final AuthUserService authService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public NativeTokenRefreser(JwtUtil jwtUtil, AuthUserService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public ResponseEntity<String> refreshJWT(String refreshToken) {
        RefreshToken fetchedToken = null;
        try{
            fetchedToken = authService.fetchRefreshTokenToken(refreshToken);
            
        }catch(BadCredentialsException e){
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>("Refresh Token does not exist", HttpStatus.UNAUTHORIZED);
        }
        
        if(fetchedToken == null || fetchedToken.getExpiresAt().isBefore(LocalDateTime.now())){

            // TODO: redirect user to login on the frontend side.
            return new ResponseEntity<>("Refresh Token Expired - re-login", HttpStatus.UNAUTHORIZED);
            
        }else if(!fetchedToken.isActive()){
            return new ResponseEntity<>("Refresh Token deactivated - login or contact an admin", HttpStatus.UNAUTHORIZED);
        }

        HttpHeaders headers = new HttpHeaders();
        User tokenOwner = fetchedToken.getUser();

        // Check if token expires soon (soon = in 1 day), if so, include the new refresh token in the response too.
        if(fetchedToken.getExpiresAt().isBefore(LocalDateTime.now().plusDays(1))){
            ResponseCookie newRefreshToken = jwtUtil.generateRefreshTokenAsCookie();

            // Save the new refresh token that is about to expire
            authService.saveRefreshToken(tokenOwner, newRefreshToken.getValue());
            headers.add(HttpHeaders.SET_COOKIE, newRefreshToken.toString());
        }
        
        // generating the new JWT (refreshed), based on the token bearer's info
        String refreshedJWT = jwtUtil.generateJWT(tokenOwner.getUserID().toString(), tokenOwner.getEmail(), tokenOwner.getFirstName() + " " + tokenOwner.getLastName());
        headers.add("Authorization", "Bearer " + refreshedJWT);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }
    
}
