package com.big_hackathon.backend_v2.repository;

import com.big_hackathon.backend_v2.model.Apartment;
import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApartmentRepository extends CrudRepository<Apartment, Long> {

    @Nonnull
    List<Apartment> findAll();

    Apartment getById(Long id);
}
