package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void postJoin() throws Exception {
        mockMvc
                .perform(post("/join")
                        .param("email", "alalstjr@naver.com")
                        .param("password", "1234")
                        .param("passwordRe","1234")
                        .param("firstName","김민석")
                        .param("postcode","1234")
                        .param("addr1","addr1")
                        .param("gender","1")
                        .param("phoneNumber","010-1234-1234")
                        .param("username","jjunpro")
                        .param("birthday","070721")
                        .param("agree1", "true")
                        .param("agree2", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}