package com.jjunpro.shop.dto;

import com.jjunpro.shop.model.ProductOrder;
import java.util.HashMap;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductOrderDTO {

    private Long    id;
    private String  ip;
    private Boolean enabled;
    private String  orderName;
    private String  orderEmail;
    private String  orderPhone;
    private String  postcode;
    private String  addr1;
    private String  addr2;
    private String  memo;
    private Short   payment;
    private String  cupon;
    private Integer point;
    private String  productIds;
    private String  productQuantitys;
    private Integer totalAmount;

    @Builder
    public ProductOrderDTO(Long id, String ip, Boolean enabled, String orderName,
            String orderEmail, String orderPhone, String postcode, String addr1, String addr2,
            String memo, Short payment, String cupon, Integer point,
            String productIds, Integer totalAmount) {
        this.id = id;
        this.ip = ip;
        this.enabled = enabled;
        this.orderName = orderName;
        this.orderEmail = orderEmail;
        this.orderPhone = orderPhone;
        this.postcode = postcode;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.memo = memo;
        this.payment = payment;
        this.cupon = cupon;
        this.point = point;
        this.productIds = productIds;
        this.totalAmount = totalAmount;
    }

    public ProductOrder toEntity() {
        return ProductOrder.builder()
                .id(id)
                .ip(ip)
                .enabled(enabled)
                .orderName(orderName)
                .orderEmail(orderEmail)
                .orderPhone(orderPhone)
                .postcode(postcode)
                .addr1(addr1)
                .addr2(addr2)
                .memo(memo)
                .payment(payment)
                .productQuantitys(productQuantitys)
                .cupon(cupon)
                .point(point)
                .productIds(productIds)
                .totalAmount(totalAmount)
                .productList(this.productHashMap())
                .build();
    }

    /* 주문하는 상품의 id, 갯수 정보를 Map 담습니다. */
    public HashMap<Long, Integer> productHashMap() {
        HashMap<Long, Integer> productMap          = new HashMap<>();
        String[]               productIdArr        = this.productIds.split(",");
        String[]               productQuantitysArr = this.productQuantitys.split(",");

        int i = 0;
        for (String productId : productIdArr) {
            productMap.put(Long.parseLong(productId), Integer.parseInt(productQuantitysArr[i]));
            i++;
        }

        return productMap;
    }
}
