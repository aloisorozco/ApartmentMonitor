package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;

import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.UserDAO;
import com.big_hackathon.backend_v2.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler{

    private final JwtUtil jwtUtil;
    private final UserDAO userDAO;
    private final UserService userService;

    OAuthSuccessHandler(JwtUtil jwtUtil, UserDAO userDAO, UserService userService){
        this.jwtUtil = jwtUtil;
        this.userDAO = userDAO;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // creating and sending out the JWT at the end of form based loggin.
        Object principal = authentication.getPrincipal();

        if(principal instanceof OidcUser){
            OidcUser user = (OidcUser) principal;
            String jwtString = user.getIdToken().getTokenValue();
            Jwt jwt = jwtUtil.decodeToken(jwtString);
            
            String fName = jwt.getClaimAsString("given_name");
            String lName = jwt.getClaimAsString("family_name");
            String email = jwt.getClaimAsString("email");

            User user_fromDB = userDAO.findByEmail(email).orElse(null);

            // Add the user if they do not exist in the DB (assuming OAUth registration)
            // TODO: really research if this is all good and safe to do.
            if(user_fromDB == null){
                // TODO: Yeah I know UUID is sketchy to put as a password, so we will need to find a way to flag users registered through OAuth, as to allow only OAuth registrations for them.
                userService.saveUser(email, UUID.randomUUID().toString(), fName, lName);
            }
            
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
