package com.big_hackathon.backend_v2.repository;

import com.big_hackathon.backend_v2.model.Account;
import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, String> {

    @Nonnull
    List<Account> findAll();

    Account getByEmail(String email);
}
