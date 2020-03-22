package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.AccountMapper;
import com.jjunpro.shop.model.Account;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;

    public Account getAccountById(Long id) {
        return accountMapper.selectAccountById(id);
    }

    public List<Account> getAllAccount() {
        return accountMapper.selectAllAccount();
    }

    public void addAccount(Account account) {
        accountMapper.insertAccount(account);
    }
}
