package com.big_hackathon.backend_v2.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import com.big_hackathon.backend_v2.filter.JwtUtil;
import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.RefreshTokenDAO;
import com.big_hackathon.backend_v2.service.AuthUserService;
import com.big_hackathon.backend_v2.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    private final UserService userService;
    private final AuthUserService authService;
    private final RefreshTokenDAO refreshTokenDAO;
    private final PasswordEncoder encoder;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthUserService authService, PasswordEncoder encoder, RefreshTokenDAO refreshTokenDAO) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.encoder = encoder;
        this.refreshTokenDAO = refreshTokenDAO;
    }

    @PostMapping("/register_user")
    public ResponseEntity<String> insertUser(@RequestBody Map<String, String> json) {
        logger.info("Register user endpoint called");

        String email = json.get("email");
        String password = json.get("password");
        String fname = json.get("fname");
        String lname = json.get("lname");
        
        if(userService.exists(email)){
            return new ResponseEntity<>("Registration failed - User with credentials already exist", HttpStatus.BAD_REQUEST);
        }

        try {
            User user = userService.saveUser(email, password, fname, lname);
            String jwt = jwtUtil.generateJWT(user.getUserID().toString(), email, fname + " " + lname);

            HttpHeaders headers = new HttpHeaders();

            // don't forget the refresh token with the JWT!
            ResponseCookie refreshToken = jwtUtil.generateRefreshTokenAsCookie();
            refreshTokenDAO.save(RefreshToken.builder().user(user).tokenValue(refreshToken.getValue()).isActive(true).build());
            headers.add(HttpHeaders.SET_COOKIE, refreshToken.toString());
            
            headers.add("Authorization", "Bearer " + jwt);

            // Adding the JWT + refresh token (cookie) into the header of the response on sucessfull registration, like that user can start making API requests.
            return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body("User registered successfully");

        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh_jwt")
    public ResponseEntity<String> refreshJWT(@CookieValue("refreshToken") String refreshToken, @RequestBody Map<String, String> payload, HttpServletRequest request) {

        UserDTO user = null;
        try{
            user = userService.getUser(payload.get("email").toString());
        }catch(Exception e){
            return new ResponseEntity<>("Bad Credentials for Refresh Token", HttpStatus.BAD_REQUEST);
        }

        RefreshToken fetchedToken = authService.fetchRefreshTokenByUserID(user.getUserID());
        if(fetchedToken == null || fetchedToken.getExpiresAt().isBefore(LocalDateTime.now())){

            // TODO: redirect user to login on the frontend side.
            return new ResponseEntity<>("Refresh Token Expired - re-login", HttpStatus.UNAUTHORIZED);
            
        }else if(!fetchedToken.isActive()){
            return new ResponseEntity<>("Refresh Token deactivated - login or contact an admin", HttpStatus.UNAUTHORIZED);
        }

        if(!encoder.matches(refreshToken, fetchedToken.getTokenValue())){
            return new ResponseEntity<>("Refresh Token Invalid", HttpStatus.UNAUTHORIZED);
        }   

        HttpHeaders headers = new HttpHeaders();

        // Check if token expires soon (soon = in 1 day), if so, include the new refresh token in the response too.
        if(fetchedToken.getExpiresAt().isBefore(LocalDateTime.now().plusDays(1))){
            ResponseCookie newRefreshToken = jwtUtil.generateRefreshTokenAsCookie();

            // Save the new refresh token that is about to expire
            refreshTokenDAO.save(RefreshToken.builder().user(user.getUser()).tokenValue(newRefreshToken.getValue()).isActive(true).build());
            headers.add(HttpHeaders.SET_COOKIE, newRefreshToken.toString());
        }
         
        String refreshedJWT = jwtUtil.generateRefreshedJWT(jwtUtil.extractJwtBearer(request));
        headers.add("Authorization", "Bearer " + refreshedJWT);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @RequestMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> payload) {

        UserDTO user = null;
        try{
            user = userService.getUser(payload.get("email").toString());
        }catch(Exception e){
            return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        }

        RefreshToken fetchedToken = authService.fetchRefreshTokenByUserID(user.getUserID());

        // Dactivate the refresh token
        if(fetchedToken.isActive()){
            fetchedToken.setActive(false);
            refreshTokenDAO.save(fetchedToken);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
