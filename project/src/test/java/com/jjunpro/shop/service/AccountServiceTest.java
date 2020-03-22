package com.jjunpro.shop.service;

import com.jjunpro.shop.model.Account;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountServiceTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AccountService accountService;

    @Test
    public void getAccountById() {
        Account accountById = accountService.getAccountById(1L);
        log.info("Account Id : " + accountById);
    }

    @Test
    public void getAllAccount() {
        List<Account> allAccount = accountService.getAllAccount();
        log.info("Account All : " + allAccount);
    }

    @Test
    public void addAccount() {
        accountService.addAccount(new Account("username", "email", "kim", "minseok"));
    }
}