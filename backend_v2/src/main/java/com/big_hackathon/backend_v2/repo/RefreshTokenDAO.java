package com.big_hackathon.backend_v2.repo;
import com.big_hackathon.backend_v2.model.RefreshToken;
import com.big_hackathon.backend_v2.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenDAO extends JpaRepository<RefreshToken, Long> {
    Optional<User> findTokenByUserId(long userID);
}
