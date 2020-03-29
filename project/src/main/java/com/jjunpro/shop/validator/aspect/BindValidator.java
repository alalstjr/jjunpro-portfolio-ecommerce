package com.jjunpro.shop.validator.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BindValidatorAspect 유효성 검사 후 최종 반환합니다.
 * <p>
 * 메소드의 매개변수에는 BindingResult 값이 필수로 참조되야 합니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindValidator {

}