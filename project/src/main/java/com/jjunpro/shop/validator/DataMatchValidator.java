package com.jjunpro.shop.validator;

import com.jjunpro.shop.enums.ColumnType;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountService;
import com.jjunpro.shop.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@RequiredArgsConstructor
public class DataMatchValidator implements ConstraintValidator<DataMatch, String> {

    private String     message;
    private ColumnType column;

    private final AccountServiceImpl accountService;

    /**
     * initialize() 메소드는 어노테이션으로 받은 값을 해당 필드에 초기화 선언을 합니다.
     */
    @Override
    public void initialize(DataMatch constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.column = constraintAnnotation.column();
    }

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        boolean           result  = false;
        Optional<Account> byUserId;
        Account           account = null;

        /* 접근하는 사용자가 익명사용자인지 확인합니다. */
        if (!SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .equals("anonymousUser")) {

            UserDetails principal = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            String username = principal.getUsername();

            account = (Account) accountService.loadUserByUsername(username);
        }

        /* { DATA UPDATE } 클라이언트 this.column 정보와 { DATA DB } 정보와 동일하지 않다면 검증실패 */
        switch (this.column) {
            case USERNAME:
                byUserId = accountService.findByUsername(value);

                /* Account Update 경우 기본 정보를 그대로 저장하기 위한 조건문 */
                if (account != null) {
                    if (byUserId.isPresent()) {
                        if (!byUserId.get().getUsername().equals(account.getUsername())) {
                            result = true;
                        }
                    }
                }
                /* Account Create 경우 */
                else {
                    if (byUserId.isPresent()) {
                        result = true;
                    }
                }
                break;

            case EMAIL:
                /* EMAIL 값은 체크는 필수가 아닙니다. */
                if (value != null && !value.isEmpty()) {
                    byUserId = accountService.findByEmail(value);

                    if (byUserId.isPresent() && account != null) {
                        if (!byUserId.get().getEmail().equals(account.getEmail())) {
                            result = true;
                        }
                    }
                }
                break;
        }

        if (result) {
            context
                    .buildConstraintViolationWithTemplate(this.message)
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
