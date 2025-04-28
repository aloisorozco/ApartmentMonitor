package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthValidationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public OAuthValidationFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // TODO: check if this is a viable way of bypassing the path restrictions
        if (path.equals("/") || path.startsWith("/oauth2") || path.equals("/login")) {
            filterChain.doFilter(request, response); // continue without JWT validation
            return;
        }

        String token = extractJwtBearer(request);
        
        if(token != null){
            Jwt jwt = jwtUtil.decodeToken(token);
            if(jwt == null){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String userName = jwt.getClaimAsString("iss") +  "-" + jwt.getClaimAsString("sub");

            // Create the user authenticaion object and added it to the spring security context (thread local)
            // this just tells spring "I manually verified this user, here is an object representing them, add it to the context, and allow them to access the APIs"
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userName, null,  List.of()); //List.of() is the list of roles - empty = defualt role (no special permisions), ex: List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            SecurityContextHolder.getContext().setAuthentication(auth);

            //Once the request is over, Spring clears the context (using SecurityContextHolder.clearContext()) - only one Authentication object at a time, per thread (means we can access cretentials anywhere in code).
        }
    }

    private String extractJwtBearer(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
