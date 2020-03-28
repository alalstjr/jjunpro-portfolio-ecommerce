package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountServiceImpl accountService;

    @Test
    public void login() throws Exception {
        String username = "alalstjr@naver.com";
        String password = "1234";

        this.createAccount(username, password);

        mockMvc
                .perform(formLogin().user(username).password(password))
                .andExpect(authenticated())
                .andDo(print());
    }

    /* 임의 유저를 생성합니다. */
    private void createAccount(String username, String password) {
        Account account = Account
                .builder()
                .email(username)
                .password(password)
                .enabled(true)
                .build();

        accountService.insertAccount(account);
    }
}