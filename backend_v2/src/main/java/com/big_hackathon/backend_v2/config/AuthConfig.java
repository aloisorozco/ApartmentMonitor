package com.big_hackathon.backend_v2.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.big_hackathon.backend_v2.filter.FormLoginAuthProvider;
import com.big_hackathon.backend_v2.filter.FormLoginAuthSuccessHandler;
import com.big_hackathon.backend_v2.filter.OAuthSuccessHandler;
import com.big_hackathon.backend_v2.filter.JwtValidationFilter;
import com.big_hackathon.backend_v2.service.AuthUserService;
import com.big_hackathon.backend_v2.filter.JwtUtil;

import lombok.SneakyThrows;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthConfig {

    @Bean
    @SneakyThrows
    SecurityFilterChain defaultSecurityFilterChain(
        HttpSecurity http, 
        OAuthSuccessHandler customSuccessHandler, 
        FormLoginAuthSuccessHandler customAuthSuccessHandler, 
        AuthenticationManager providers,
        JwtUtil jwtUtil, 
        AuthUserService userService){
        
        // Setting up CSRF + Form base login
        http.csrf(csrf -> csrf.disable())
            .formLogin(login -> login.loginPage("/login").successHandler(customAuthSuccessHandler)) // Default loggin w/appropriate success handler
            .userDetailsService(userService)
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests.requestMatchers("/auth/**").permitAll();
                authorizeRequests.anyRequest().authenticated();

            }).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            
        
        // Setting up the custom JWT validation filter so that all API calls are validated
        http.addFilterBefore(new JwtValidationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
        // Setting up OAuth filters
        http.oauth2Login(oauth -> oauth.successHandler(customSuccessHandler)); // OAuth loggin w/appropriate success handler

        // Setting up Authentication Managers
        http.authenticationManager(providers);

        // Setting all non authroized requests to make Spring return a HTTP 401, instead of redirecting to loggin, since Spring is our API backend. 
        http.exceptionHandling(ex -> ex
            .defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**")
            )
        );

        return http.build();
    }

    // Creating the AuthenticationManager bean that will manage multiple AuthenticationProviders
    @Bean
    AuthenticationManager providers(FormLoginAuthProvider formLoginProvider){
        List<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(formLoginProvider);

        // ... and one for OIDC!
        providers.add(new OidcAuthorizationCodeAuthenticationProvider(new RestClientAuthorizationCodeTokenResponseClient(), new OidcUserService()));
        return new ProviderManager(providers);
    }

    @Bean
    PasswordEncoder loginPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
}
