package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;

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

        if(principal instanceof OidcUser){
            OidcUser user = (OidcUser) principal;
            String jwtString = user.getIdToken().getTokenValue();
            // Jwt jwt = jwtUtil.decodeToken(jwtString);
            
            // String fName = jwt.getClaimAsString("given_name");
            // String lName = jwt.getClaimAsString("last_name");
            // String email = jwt.getClaimAsString("email");

            // TODO: check if the user is in the DB, if not, add them to the DB as a new user. Should we even register the user here? could be dangerous, should look into a registration flow.
            // but definetly need to check if user exist in DB, otherwise should NOT return a JWT back to the user!
            
            // returning the JWT back to the user.
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + jwtString + "\"}");
            response.flushBuffer();

        }else{
            throw new ServletException("Issues with Form Login JWT generation");
        }
    }

    
}
