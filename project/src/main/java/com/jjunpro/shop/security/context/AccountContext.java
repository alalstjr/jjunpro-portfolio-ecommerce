package com.jjunpro.shop.security.context;

import com.jjunpro.shop.model.Account;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AccountContext extends User {

    private Account account;

    public AccountContext(Account account) {
        super(
                account.getEmail(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getUserRole()))
        );

        /* Domain account 접근할 수 있도록 추가 */
        this.account = account;
    }

    public AccountContext(String username, String role) {
        super(
                username,
                "default password",
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    public Account getAccount() {
        return account;
    }
}