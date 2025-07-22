package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import com.big_hackathon.backend_v2.model.SpringSUser;
import com.big_hackathon.backend_v2.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// OncePerRequestFilter = filter guaranteed to execute only once per request
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtValidationFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        
        if (path.equals("/") || path.startsWith("/oauth2") || path.equals("/login")) {
            return true; // true = do not filter request to check if it has JWT on loggin
        }

        return false; // false = filter request to check if it has JWT
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractJwtBearer(request);
        
        if(token != null){
            Jwt jwt = jwtUtil.decodeToken(token);
            if(jwt == null){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String userName = jwt.getClaimAsString("email");
            String fullName = jwt.getClaimAsString("name");
            String[] names = fullName.split(" ");

            User user = User.builder()
                .email(userName)
                .firstName(names[0])
                .lastName(names[1])
                .build();

            SpringSUser userDetails = new SpringSUser(user);

            // Create the user authenticaion object and added it to the spring security context (thread local)
            // this just tells spring "I manually verified this user, here is an object representing them, add it to the context, and allow them to access the APIs"
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,  List.of()); //List.of() is the list of roles - empty = defualt role (no special permisions), ex: List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            // continue the filter chain.
            filterChain.doFilter(request, response);
        }else{
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing JWT token");
            return;
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
