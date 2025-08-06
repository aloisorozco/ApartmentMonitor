package com.big_hackathon.backend_v2.service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.SpringSUser;
import com.big_hackathon.backend_v2.model.User;
import com.big_hackathon.backend_v2.repo.RefreshTokenDAO;
import com.big_hackathon.backend_v2.repo.UserDAO;

@Service
public class AuthUserService implements UserDetailsService{

    private final UserDAO userRepo;
    private final RefreshTokenDAO refreshTokenRepo;

    public AuthUserService(UserDAO userRepo, RefreshTokenDAO refreshTokenRepo){
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    // TODO: ask nico to make a username field in the db -> username is just a unique key other than the ID.
    @Override
    public SpringSUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + ", not found"));

        return new SpringSUser(user);
    }

    public RefreshToken fetchRefreshTokenToken(String tokenID) throws BadCredentialsException{
        return refreshTokenRepo.findTokenByTokenValue(tokenID).orElseThrow(() -> new BadCredentialsException("Refresh token does not exist in DB"));
    }

    public RefreshToken fetchRefreshTokenByUserID(Long userID) throws BadCredentialsException{
        return refreshTokenRepo.findTokenByUserID(userID).orElseThrow(() -> new BadCredentialsException("Refresh token does not exist in DB for user ID " + userID));
    }

    @Transactional
    public User registerUser(User user, String refreshTokenValue) {
        userRepo.save(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenValue(refreshTokenValue);
        token.setActive(true);

        refreshTokenRepo.save(token);

        return user;
    }

    public void saveRefreshToken(User user, String refreshTokenValue) {

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenValue(refreshTokenValue);
        token.setActive(true);

        refreshTokenRepo.save(token);
    }

    public void updateRefreshToken(RefreshToken updatedToken) {
        refreshTokenRepo.save(updatedToken);
    }
    
}
