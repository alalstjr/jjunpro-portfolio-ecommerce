package com.jjunpro.shop.validator;

import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.StringBuilderUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ProductQuantityValidator implements ConstraintValidator<ProductQuantity, Object> {

    private String message;
    private String id;
    private String quantity;

    private final ProductServiceImpl productService;
    private final StringBuilderUtil  stringBuilderUtil;

    @Override
    public void initialize(ProductQuantity constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.id = constraintAnnotation.id();
        this.quantity = constraintAnnotation.quantity();
    }

    @SneakyThrows
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        Class<?> clazz         = obj.getClass();
        Field    fieldId       = clazz.getDeclaredField("setId");
        Field    fieldQuantity = clazz.getDeclaredField("setQuantity");

        fieldId.setAccessible(true);
        fieldQuantity.setAccessible(true);

        String idVal       = (String) fieldId.get(obj);
        String quantityVal = (String) fieldQuantity.get(obj);

        /*
         * { 1, 2, 3 } 문자열을 배열로 변환 후 짝을 지어서 Map 담아서 관리하도록 합니다.
         * 순환 기준이되는 값은 id 입니다.
         */
        String[]           idArr         = this.stringBuilderUtil.classifyUnData(idVal);
        String[]           quantityArr   = this.stringBuilderUtil.classifyUnData(quantityVal);
        boolean            quantityCheck = true;
        boolean            enabledCheck  = true;
        Map<Long, Integer> productMap    = new HashMap<>();

        int i = 0;
        for (String id : idArr) {
            /* 상품과 수량값이 존재하는지 확인 후 map 생성 */
            if (!id.isEmpty() && !quantityArr[i].isEmpty()) {
                productMap.put(Long.parseLong(id), Integer.parseInt(quantityArr[i]));
                i++;
            } else {
                quantityCheck = false;
            }
        }

        for (Long id : productMap.keySet()) {
            Integer quantity = productMap.get(id);

            Optional<Product> dbProduct = this.productService.findById(id, false);

            if (dbProduct.isPresent()) {
                /* 상품이 판매가능 상태인지 체크합니다. */
                enabledCheck = dbProduct.get().getEnabled();

                /* DB 존재하는 상품의 수량보다 클라이언트 수량이 더 많은경우 false */
                quantityCheck = dbProduct.get().getQuantity() >= quantity;
            }

            if (!quantityCheck || !enabledCheck) {
                break;
            }
        }

        if (!quantityCheck || !enabledCheck) {
            context
                    .buildConstraintViolationWithTemplate(this.message)
                    .addPropertyNode(this.quantity)
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
