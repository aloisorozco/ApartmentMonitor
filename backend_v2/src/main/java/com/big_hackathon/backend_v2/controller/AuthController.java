package com.big_hackathon.backend_v2.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.big_hackathon.backend_v2.DTO.UserDTO;
import com.big_hackathon.backend_v2.controller.refreshers.TokenRefreshersFactory;
import com.big_hackathon.backend_v2.filter.JwtUtil;
import com.big_hackathon.backend_v2.model.Apartment;
import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.service.AuthUserService;
import com.big_hackathon.backend_v2.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final TokenRefreshersFactory refresherFactory;

    private final UserService userService;
    private final AuthUserService authService;
    private final PasswordEncoder encoder;
    

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthUserService authService, PasswordEncoder encoder, TokenRefreshersFactory refresherFactory) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.encoder = encoder;
        this.refresherFactory = refresherFactory;
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
            User user = User
                .builder()
                .email(email)
                .hashedPassword(encoder.encode(password))
                .firstName(fname)
                .lastName(lname)
                .apartments(new ArrayList<Apartment>())
                .build();

            HttpHeaders headers = new HttpHeaders();

            // don't forget the refresh token with the JWT!
            ResponseCookie refreshToken = jwtUtil.generateRefreshTokenAsCookie();
            user = authService.registerUser(user, refreshToken.getValue());

            headers.add(HttpHeaders.SET_COOKIE, refreshToken.toString());
            
            String jwt = jwtUtil.generateJWT(user.getUserID().toString(), email, fname + " " + lname);
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
    public ResponseEntity<String> refreshJWT(@CookieValue("refreshToken") String refreshToken, @RequestHeader("Authorization") String authHeader) {
        
        String jwt = jwtUtil.extractJwtBearer(authHeader);
        String tokenIss = jwtUtil.getIssuerId(jwt);
        return refresherFactory.getTokenRefresher(tokenIss).refreshJWT(refreshToken);
    }

    @RequestMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> payload) {

        UserDTO user = null;
        try{
            user = userService.getUser(payload.get("email").toString());
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        }

        RefreshToken fetchedToken = null;
        try{
            fetchedToken = authService.fetchRefreshTokenByUserID(user.getUserID());
            
        }catch(BadCredentialsException e){
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>("Refresh Token does not exist", HttpStatus.UNAUTHORIZED);
        }

        // Dactivate the refresh token
        if(fetchedToken.isActive()){
            fetchedToken.setActive(false);
            authService.updateRefreshToken(fetchedToken);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
