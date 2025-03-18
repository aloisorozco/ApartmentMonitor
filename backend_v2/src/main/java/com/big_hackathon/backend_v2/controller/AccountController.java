package com.big_hackathon.backend_v2.controller;

import com.big_hackathon.backend_v2.model.Account;
import com.big_hackathon.backend_v2.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    @GetMapping("/")
    public List<Account> listAccounts() {
        logger.info("listAccounts endpoint called");
        return accountService.listAccounts();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable String email) {
        logger.info("getAccount endpoint called");
        return accountService.getAccount(email);
    }

    @PostMapping("/")
    public Account insertAccount(@RequestBody Account account) {
        logger.info(account.toString());
        return accountService.insertAccount(account);
    }

    @PutMapping("/")
    public Account updateAccount(@RequestBody Account account) {
        logger.info("updateAccount endpoint called");
        return accountService.updateAccount(account);
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable String id) {
        logger.info("deleteAccount endpoint called");
        accountService.deleteAccount(id);
        return "OK";
    }

}
