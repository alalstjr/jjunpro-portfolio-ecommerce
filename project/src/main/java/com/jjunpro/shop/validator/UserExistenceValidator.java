package com.jjunpro.shop.validator;

import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.util.AccountUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * Spring Security Context 접근하여 로그인된 사용자의 { DATA } 정보를 받아옵니다.
 * <p>
 * AccountUtill 접근한 사용자가 DB 에 존재하는지 확인하는 메소드입니다.
 * <p>
 * 사용자의 { DATA account } 가 DB 에 존재하는지 확인합니다.
 */
@RequiredArgsConstructor
public class UserExistenceValidator implements ConstraintValidator<UserExistence, Object> {

    private       String          message;
    private       String          id;
    private final SecurityContext securityContext = SecurityContextHolder.getContext();
    private final AccountUtil     accountUtil;

    @Override
    public void initialize(UserExistence constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.id = constraintAnnotation.id();
    }

    @Override
    public boolean isValid(
            Object value,
            ConstraintValidatorContext context
    ) {
        Optional<Account> accountData = accountUtil.accountInfo(securityContext.getAuthentication());

        if (accountData.isEmpty()) {
            context
                    .buildConstraintViolationWithTemplate(this.message)
                    .addPropertyNode(this.id)
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}
