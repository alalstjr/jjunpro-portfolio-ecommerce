package com.jjunpro.shop.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 상품의 남은 수량과 클라이언트에서 요구하는 수량의 값을 비교하여 처리합니다.
 * <p></p>
 * 동시에 상품의 판매상태도 체크합니다.
 */
@Documented
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProductQuantityValidator.class)
public @interface ProductQuantity {

    String message() default "수량이 존재하지 않습니다.";

    String id();

    String quantity();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
