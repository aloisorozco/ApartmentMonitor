package com.big_hackathon.backend_v2.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuthSuccessHandler implements AuthenticationSuccessHandler{

    private final OAuth2AuthorizedClientService clientService;

    @Autowired
    public CustomOAuthSuccessHandler(OAuth2AuthorizedClientService clientService){
        this.clientService = clientService;
    }
    
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
            }

            // TODO: check if user exists in DB, save if not based on iss and sub.

            response.setContentType("application/json");
            response.getWriter().write("{\"id_token\": \"" + idToken + "\"}");
            response.getWriter().flush();
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to Auth user");
        }
    }   
}
