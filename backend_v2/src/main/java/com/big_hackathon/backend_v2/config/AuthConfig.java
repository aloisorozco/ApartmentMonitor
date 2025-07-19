package com.big_hackathon.backend_v2.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.big_hackathon.backend_v2.filter.FormLoginAuthProvider;
import com.big_hackathon.backend_v2.filter.FormLoginAuthSuccessHandler;
import com.big_hackathon.backend_v2.filter.CustomOAuthSuccessHandler;
import com.big_hackathon.backend_v2.filter.OAuthValidationFilter;
import com.big_hackathon.backend_v2.service.AuthUserService;
import com.big_hackathon.backend_v2.filter.JwtUtil;

import lombok.SneakyThrows;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthConfig {

    @Autowired
    private JwtUtil jwtUtil; 

    @Autowired
    private AuthUserService userService;

    @Autowired
    private FormLoginAuthProvider formLoginProvider;

    @Bean
    @SneakyThrows
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CustomOAuthSuccessHandler customSuccessHandler, FormLoginAuthSuccessHandler customAuthSuccessHandler){
        
        // Setting up CSRF + Form base login
        http.csrf(csrf -> csrf.disable())
            .formLogin(login -> login.loginPage("/login").successHandler(customAuthSuccessHandler)) // Default loggin w/appropriate success handler
            .userDetailsService(userService)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        // Setting up OAuth filters
        http.addFilterBefore(new OAuthValidationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(oauth -> oauth.successHandler(customSuccessHandler)); // OAuth loggin w/appropriate success handler

        // Setting up Authentication Managers
        http.authenticationManager(providers(formLoginProvider));
        return http.build();
    }

    // Creating the AuthenticationManager bean that will manage multiple AuthenticationProviders
    // TODO: when done, add the CustomOauthProvider here too, so spring can manager it.
    @Bean
    AuthenticationManager providers(FormLoginAuthProvider formLoginProvider){
        return new ProviderManager(List.of(formLoginProvider));
    }


    // TODO: Changed to a stronger password encoder down the line - SHA-256 is deprecated, using it rn just for testing.
    @Bean
    PasswordEncoder loginPasswordEncoder(){
        return new StandardPasswordEncoder();
    }
    
}
