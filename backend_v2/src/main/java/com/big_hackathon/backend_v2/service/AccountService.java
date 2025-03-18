package com.big_hackathon.backend_v2.service;

import com.big_hackathon.backend_v2.model.Account;
import com.big_hackathon.backend_v2.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;
    
    public List<Account> listAccounts() {
        return accountRepo.findAll();
    }

    public Account getAccount(String email) {
        return accountRepo.getByEmail(email);
    }

    public Account insertAccount(Account account) {
        return accountRepo.save(account);
    }

    public Account updateAccount(Account account) {
        return accountRepo.save(account);
    }

    public void deleteAccount(String id) {
        accountRepo.deleteById(id);
    }
}
