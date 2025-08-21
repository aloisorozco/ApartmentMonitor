package com.big_hackathon.backend_v2.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JSONUsernamePassowrdAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    @Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
        ObjectMapper mapper = new ObjectMapper();
        LoginRequest loginRequest = null;
        try {
            loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Issue mapping login attributes: " + e.toString());
        }

		String username = loginRequest.getEmail() != null ? loginRequest.getEmail().trim() : "";
		String password = loginRequest.getPassword() != null ? loginRequest.getPassword().trim() : "";
		UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);

		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
}


// POJO to deserialize the Json object containing credentials.
class LoginRequest {
    private String email;
    private String password;

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }
}