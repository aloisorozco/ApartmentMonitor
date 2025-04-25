package com.big_hackathon.backend_v2.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.controller.ApartmentController;
import com.big_hackathon.backend_v2.repo.UserDAO;
import com.big_hackathon.backend_v2.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuthSuccessHandler implements AuthenticationSuccessHandler{

    private final OAuth2AuthorizedClientService clientService;
    private final UserService usrService;
    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    @Autowired
    public CustomOAuthSuccessHandler(OAuth2AuthorizedClientService clientService, UserService usrService){
        this.clientService = clientService;
        this.usrService = usrService;
    }
    
    //Custom Auth Success filter - Spring Security allows us to override the default succes filter (which just does some cleanup) and implement our own, which here
    //reurns the JWT to the frontend, so that every API call is made stateless, and requires the JWT bearer token.

    //We cannot overwrite Spring to NOT create a session, so we still need to add a JWT token validation filter, that will check if the bearer token is valid or not
    //and block the API call otherwise!
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());
        if(client != null){
            String idToken = null;
            if(authToken.getPrincipal() instanceof OidcUser){
                OidcUser oidcUsr = (OidcUser) authToken.getPrincipal();
                idToken = oidcUsr.getIdToken().getTokenValue();
                
                String usrID = oidcUsr.getClaim("iss") + "-" + oidcUsr.getClaim("sub");

                if(!usrService.exists(usrID)){
                    String email = oidcUsr.getClaim("email");
                    String[] fullName = oidcUsr.getClaim("name").toString().split(" ");
                    String fname = fullName.length == 1 ? fullName[0] : "";
                    String lname = fullName.length == 2 ? fullName[1] : "";

                    usrService.saveUser(usrID, email, "",fname, lname);
                    logger.info("[OAuth] Created user with email " + email + "on first sign in using OAuth.");
                }
                logger.info("[OAuth] user already exist, authenticated using OAuth.");
            }

            response.setContentType("application/json");
            response.getWriter().write("{\"id_token\": \"" + idToken + "\"}");
            response.getWriter().flush();
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to Auth user");
        }
    }   
}
