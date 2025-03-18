package com.big_hackathon.backend_v2.repository;

import com.big_hackathon.backend_v2.model.Apartment;
import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends CrudRepository<Apartment, String> {

    @Nonnull
    List<Apartment> findAll();

    Apartment getById(String id);
}
