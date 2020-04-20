package com.jjunpro.shop.aspect;

import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 구매 정보를 Logger 기록을 남깁니다.
 */
@Aspect
@Component
public class ProductOrderSetAspect {

    private static final Logger logger = LoggerFactory.getLogger(ProductOrderServiceImpl.class);

    @Around("@annotation(com.jjunpro.shop.aspect.ProductOrderSet)")
    public Object validator(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = joinPoint.proceed();

        ProductOrder productOrder = null;
        String       parameterName;

        Object[] joinPointArgs = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method          method    = signature.getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {
            parameterName = method.getParameters()[i].getName();
            if (parameterName.equals("productOrder")) {
                productOrder = (ProductOrder) joinPointArgs[i];
                break;
            }
        }

        if (productOrder != null) {
            DateTime dt  = new DateTime();
            String   now = dt.toString("yyyy-MM-dd HH:mm:ss");

            logger.info("ProductOrder ======== {");
            logger.info("구매주문서 id : " + productOrder.getId());
            logger.info("구매자 id : " + productOrder.getAccountId());
            logger.info("구매자 ip : " + productOrder.getIp());
            logger.info("주문 상태 : " + productOrder.getOrderState());
            logger.info("시간 : " + now);
            logger.info("}");
        }

        return proceed;
    }
}