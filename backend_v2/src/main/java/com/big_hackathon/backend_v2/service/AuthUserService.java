package com.big_hackathon.backend_v2.service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.SpringSUser;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.RefreshTokenDAO;
import com.big_hackathon.backend_v2.repo.UserDAO;

@Component
public class AuthUserService implements UserDetailsService{

    private final UserDAO userRepo;
    private final RefreshTokenDAO refreshToken;

    public AuthUserService(UserDAO userRepo, RefreshTokenDAO refreshToken){
        this.userRepo = userRepo;
        this.refreshToken = refreshToken;
    }

    // TODO: ask nico to make a username field in the db -> username is just a unique key other than the ID.
    @Override
    public SpringSUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + ", not found"));

        return new SpringSUser(user);
    }

    public RefreshToken fetchRefreshTokenByUserID(Long userID){
        return refreshToken.findTokenByUserID(userID).orElseThrow(() -> new UsernameNotFoundException("Refresh token does not exist for user with ID " + userID)); // TODO: confirm it's ok for us to expose userID like this
    }
}
