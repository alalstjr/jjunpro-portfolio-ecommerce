package com.jjunpro.shop.validator;

import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security Context 접근하여 로그인된 사용자의 { DATA } 정보를 받아옵니다.
 * <p>
 * productl 접근한 사용자가 DB 에 존재하는지 확인하는 메소드입니다.
 * <p>
 * 사용자의 { DATA account } 가 DB 에 존재하는지 확인합니다.
 */
@RequiredArgsConstructor
public class UserExistenceValidator implements ConstraintValidator<UserExistence, Object> {

    private       String             message;
    private       String             id;
    private final AccountServiceImpl accountService;

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
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        AccountContext userDetails = (AccountContext) this.accountService
                .loadUserByUsername(principal.getUsername());

        if (userDetails.getAccount() == null) {
            context
                    .buildConstraintViolationWithTemplate(this.message)
                    .addPropertyNode(this.id)
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}
