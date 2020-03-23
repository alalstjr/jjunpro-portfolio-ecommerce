package com.jjunpro.shop.validator;

import com.jjunpro.shop.dto.UserFormDTO;
import com.jjunpro.shop.mapper.AccountMapper;
import com.jjunpro.shop.model.Account;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final AccountMapper accountMapper;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == UserFormDTO.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
//        UserFormDTO formDTO = (UserFormDTO) target;
//
//        /* 검증하는 단계에서 에러가 존재하는경우 error message 를 출력합니다. */
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email is required");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "User name is required");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "First name is required");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "", "Last name is required");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password is required");
//
//        if (errors.hasErrors()) {
//            return;
//        }
//
//        /* 옭바른 Email 정보인지 유효성 검사 */
//        if (!new EmailValidator().isValid(formDTO.getEmail(), null)) {
//            errors.rejectValue("email", "", "Email is not valid");
//            return;
//        }
//
//        Account userAccount = accountMapper.findByUsername(formDTO.getUsername());
//        if (userAccount != null) {
//            if (formDTO.getId() == null || !formDTO.getId().equals(userAccount.getId())) {
//                errors.rejectValue("userName", "", "User name is not available");
//                return;
//            }
//        }
//
//        userAccount = accountMapper.findByEmail(formDTO.getEmail());
//        if (userAccount != null) {
//            if (formDTO.getId() == null || !formDTO.getId().equals(userAccount.getId())) {
//                errors.rejectValue("email", "", "Email is not available");
//                return;
//            }
//        }
    }
}
