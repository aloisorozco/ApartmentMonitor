package com.big_hackathon.backend_v2.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.big_hackathon.backend_v2.model.User;

// collection = db entry that contains nothing more than documents
// document = "end point" that contains properties and other documents/collections

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
