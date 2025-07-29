package com.big_hackathon.backend_v2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.big_hackathon.backend_v2.repo.UserRepo;
import com.big_hackathon.backend_v2.service.AuthUserService;

@Configuration
public class SecurityConfig {
    
    @Autowired
    private UserRepo userRepo;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{

        // TODO: Add further security logic/filters here
        
        // all incomming HTTP request need to be authenticated
        http.authorizeHttpRequests((c) -> {
            c
            .requestMatchers("/api/**").permitAll()
            .anyRequest().authenticated();
        });

        // Setting custom user service
        http.userDetailsService(new AuthUserService(userRepo));

        // Enable testing with Postman. Remove for production
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
