package com.jjunpro.shop.service;

import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.mapper.AccountMapper;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.security.context.AccountContext;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper   accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<Account> findById(Long id) {
        return accountMapper.findById(id);
    }

    @Override
    public List<Account> findAll() {
        return accountMapper.findAll();
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return accountMapper.findByUsername(username);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountMapper.findByEmail(email);
    }

    @Override
    public Optional<Account> findEmailByUsernameAndPhoneNumber(String username,
            String phoneNumber) {
        return accountMapper.findEmailByUsernameAndPhoneNumber(username, phoneNumber);
    }

    @Override
    public Account insertAccount(Account account) {
        account.setEnabled(true);
        account.setUserRole(UserRole.USER);
        account.setPoint(10000);
        if (account.getPassword() != null) {
            account.encodePassword(passwordEncoder);
        }

        accountMapper.insertAccount(account);

        Optional<Account> accountDB = this.findById(account.getId());

        if (accountDB.isPresent()) {
            return accountDB.get();
        } else {
            throw new UsernameNotFoundException("INSERT 과정에 문제가 생겼습니다.");
        }
    }

    @Override
    public void updateAccount(Account account) {
        accountMapper.updateAccount(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Account> account = accountMapper.findByEmailAndEnabled(username, true);

        /* 가입된 유저가 존재하는 경우 */
        if (account.isPresent()) {
            /* OAuth2 로그인 접근으로 password 존재하지 않는경우 생성자 null 접근 못하도록 처리 */
            if (account.get().getPassword() == null || account.get().getPassword().isEmpty()) {
                account.get().setPassword("default password");
            }

            return new AccountContext(account.get());
        } else {
            throw new UsernameNotFoundException(username + "정보를 찾을 수 없습니다.");
        }
    }

    @Override
    public Integer findCountByAll() {
        return this.accountMapper.findCountByAll();
    }
}
