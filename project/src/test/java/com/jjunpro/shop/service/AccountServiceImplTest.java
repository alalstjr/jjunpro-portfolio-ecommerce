package com.jjunpro.shop.service;

import static org.junit.Assert.*;

import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceImplTest {

    @Autowired
    AccountService accountService;

    @Test
    public void loadUserByUsername() {
        accountService.loadUserByUsername("alalstjr@naver.com");
    }

    @Test
    public void insetAccount() {
        Account build = Account.builder()
                .lastName("first")
                .firstName("last")
                .username("username")
                .password("password")
                .build();

        Account insertAccount = accountService.insertAccount(build);
        System.out.println(insertAccount);
    }
}