package com.jjunpro.shop.util;

import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountUtil {

    private final AccountService accountService;

    /**
     * 유저 정보를 { DB 에서 } 가져옵니다.
     * Controller 에서 사용가능합니다.
     * 매개변수로 Authentication 필수 입니다.
     */
    public Optional<Account> accountInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return accountService.findByUsername(userDetails.getUsername());
    }
}
