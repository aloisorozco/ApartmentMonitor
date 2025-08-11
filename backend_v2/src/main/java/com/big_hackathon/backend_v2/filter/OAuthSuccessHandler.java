package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    private final OAuth2AuthorizedClientService authorizedClientService;


    OAuthSuccessHandler(JwtUtil jwtUtil, UserDAO userDAO, UserService userService, OAuth2AuthorizedClientService authorizedClientService){
        this.jwtUtil = jwtUtil;
        this.userDAO = userDAO;
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // creating and sending out the JWT at the end of form based loggin.
        Object principal = authentication.getPrincipal();

        if(principal instanceof OidcUser){
            OidcUser user = (OidcUser) principal;
            String jwtString = user.getIdToken().getTokenValue();
            Jwt jwt = jwtUtil.decodeToken(jwtString);

            if(jwt == null){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            
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

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            String refreshTokenValue = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null;
            ResponseCookie refreshTokenCookie = jwtUtil.generateRefreshTokenAsCookie(refreshTokenValue);
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());  

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
