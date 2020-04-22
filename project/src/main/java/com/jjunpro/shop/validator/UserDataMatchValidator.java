package com.jjunpro.shop.validator;

import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountService;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import com.jjunpro.shop.util.AccountUtil;
import java.lang.reflect.Field;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * Spring Security Context 접근하여 로그인된 사용자의 { DATA } 정보를 받아옵니다.
 * <p>
 * Account 접근한 사용자가 DB 에 존재하는지 확인하는 메소드입니다.
 * <p>
 * 최종적으로 { 접근하려는 DATA id } 그리고 { 접근하는 DB DATA id } 같은지 비교합니다.
 */
@RequiredArgsConstructor
public class UserDataMatchValidator implements ConstraintValidator<UserDataMatch, Object> {

    private String     message;
    private String     id;
    private DomainType domain;

    private final AccountServiceImpl      accountService;
    private final ProductOrderServiceImpl productOrderService;
    private final AccountUtil             accountUtil;

    @Override
    public void initialize(UserDataMatch constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.id = constraintAnnotation.id();
        this.domain = constraintAnnotation.domain();
    }

    @Override
    public boolean isValid(
            Object value,
            ConstraintValidatorContext context
    ) {
        boolean valid = true;
        Long    idCheck;

        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        AccountContext userDetails = (AccountContext) this.accountService
                .loadUserByUsername(principal.getUsername());

        if (!accountUtil.adminCheck(principal)) {
            try {
                Field declaredField = value.getClass().getDeclaredField(this.id);
                declaredField.setAccessible(true);
                idCheck = (Long) declaredField.get(value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (idCheck != null) {
                valid = this.dbDataMatch(
                        idCheck,
                        userDetails.getAccount(),
                        this.domain
                );
            }

            if (!valid) {
                context
                        .buildConstraintViolationWithTemplate(this.message)
                        .addPropertyNode(this.id)
                        .addConstraintViolation();
            }
        }

        return valid;
    }

    /**
     * Domain 의 특정 { id } DATA 값이 DB 에 존재하는지 확인하는 메소드입니다. String checkDomain 값은 검색하는 Domain 의
     * 이름값입니다. DATA 가 존재하지 않을경우 NULL 을 반환합니다.
     */
    public boolean dbDataMatch(
            Long id,
            Account accountData,
            DomainType domainType
    ) {
        Long data = null;

        // 해당 데이터의 작성자 {id} 값을 가져옵니다.
        switch (domainType) {
            case ACCOUNT:
                Optional<Account> dbAccountData = this.accountService.findById(id);
                if (dbAccountData.isPresent()) {
                    data = dbAccountData.get().getId();
                }
                break;

            case PRODUCTORDER:
                Optional<ProductOrder> dbProductOrder = this.productOrderService.findById(id);
                if (dbProductOrder.isPresent()) {
                    data = dbProductOrder.get().getAccountId();
                }
                break;

            default:
                break;
        }

        // 해당 데이터의 작성자가 맞는지 검사합니다.
        if (data != null) {
            return data.equals(accountData.getId());
        }

        return false;
    }
}
