package com.big_hackathon.backend_v2.filter;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.service.AuthUserService;

// Default Authentication provider for form login
@Component
public class CustomAuthProvider implements AuthenticationProvider{

    private final PasswordEncoder passwordEncoder;
    private final AuthUserService userDetailsService;
    
    public CustomAuthProvider(PasswordEncoder passwordEncoder,AuthUserService userService){
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userService;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String usrName = auth.getName();
        String password = auth.getCredentials().toString();

        UserDetails user = userDetailsService.loadUserByUsername(usrName);

        if(passwordEncoder.matches(password, user.getPassword())){
            return new UsernamePasswordAuthenticationToken(usrName, password, user.getAuthorities());
        }else{
            throw new BadCredentialsException("Credentials are Bad");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
