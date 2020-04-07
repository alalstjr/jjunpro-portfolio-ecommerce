package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductOrder {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Boolean       enabled;
    private String        orderName;
    private String        orderEmail;
    private String        orderPhone;
    private String        postcode;
    private String        addr1;
    private String        addr2;
    private String        memo;
    private Short         payment;
    private String        cupon;
    private Integer       point;
    private Integer       totalAmount;
    private String        productIds;
    private String        productQuantitys;
    private String        productAmounts;
    private Long          accountId;

    private Map<Long, Integer> productList = new HashMap<>();

    @Builder
    public ProductOrder(Long id, String ip, LocalDateTime createdDate,
            LocalDateTime modifiedDate, Boolean enabled, String orderName, String orderEmail,
            String orderPhone, String postcode, String addr1, String addr2, String memo,
            Short payment, String productQuantitys, String cupon, Integer point,
            Integer totalAmount,
            String productIds, Long accountId,
            Map<Long, Integer> productList) {
        this.id = id;
        this.ip = ip;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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
        this.totalAmount = totalAmount;
        this.productIds = productIds;
        this.productQuantitys = productQuantitys;
        this.accountId = accountId;
        this.productList = productList;
    }
}
