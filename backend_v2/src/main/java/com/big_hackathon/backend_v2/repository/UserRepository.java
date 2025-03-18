package com.big_hackathon.backend_v2.repository;

import com.big_hackathon.backend_v2.model.Apartment;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Apartment, Long> {
}
