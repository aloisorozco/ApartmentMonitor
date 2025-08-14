package com.big_hackathon.backend_v2.repo;

import java.util.Optional;
import com.big_hackathon.backend_v2.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findByListingID(Long listingID);
}
