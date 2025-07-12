package com.big_hackathon.backend_v2.repo;

import com.big_hackathon.backend_v2.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentDAO extends JpaRepository<Apartment, Long> {
}
