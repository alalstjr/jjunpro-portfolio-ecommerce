package com.jjunpro.shop.validator;

import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountServiceImpl;
import java.lang.reflect.Field;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Spring Security Context 접근하여 로그인된 사용자의 { DATA } 정보를 받아옵니다.
 * <p>
 * Account 접근한 사용자가 DB 에 존재하는지 확인하는 메소드입니다.
 * <p>
 * 최종적으로 { 접근하려는 DATA id } 그리고 { 접근하는 DB DATA id } 같은지 비교합니다.
 */
@RequiredArgsConstructor
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String  message;
    private String  password;
    private String  passwordRe;
    private String  oldPassword;
    private boolean encoder;

    private final PasswordEncoder    passwordEncoder;
    private final AccountServiceImpl accountService;

    /*
     * initialize() 메소드는 어노테이션으로 받은 값을 해당 필드에 초기화 선언을 합니다.
     * */
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.password = constraintAnnotation.password();
        this.passwordRe = constraintAnnotation.passwordRe();
        this.oldPassword = constraintAnnotation.oldPassword();
        this.encoder = constraintAnnotation.encoder();
    }

    @Override
    public boolean isValid(
            Object value,
            ConstraintValidatorContext context
    ) {
        boolean valid;

        /*
         * passwordCheck[0] : password
         * passwordCheck[1] : passwordRe
         * passwordCheck[2] : oldPassword
         * */
        final String[] passwordCheck = new String[3];

        Field[] declaredFields = value.getClass().getDeclaredFields();
        Arrays.stream(declaredFields).forEach(field -> {
            field.setAccessible(true);

            if (field.getName().equals("password")) {
                try {
                    passwordCheck[0] = (String) field.get(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getName().equals("passwordRe")) {
                try {
                    passwordCheck[1] = (String) field.get(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getName().equals("oldPassword")) {
                try {
                    passwordCheck[2] = (String) field.get(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        /* 입력한 password || passwordRe 가 동일한지 확인합니다. */
        valid = passwordCheck[0] == null && passwordCheck[1] == null
                || passwordCheck[0] != null && passwordCheck[0].equals(passwordCheck[1]);

        if (!valid) {
            context
                    .buildConstraintViolationWithTemplate(this.message)
                    .addPropertyNode(this.password)
                    .addConstraintViolation();

            return false;
        }

        /* 비밀번호 변경인 경우 oldPassword 가 동일한지 확인합니다. */
        if (this.encoder) {
            UserDetails principal = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            AccountContext account = (AccountContext) this.accountService
                    .loadUserByUsername(principal.getUsername());

            valid = passwordEncoder.matches(
                    passwordCheck[2],
                    account.getAccount().getPassword()
            );

            if (!valid) {
                context
                        .buildConstraintViolationWithTemplate("이전 비밀번호가 같지 않습니다.")
                        .addPropertyNode(this.oldPassword)
                        .addConstraintViolation();
            }
        }

        return valid;
    }
}
