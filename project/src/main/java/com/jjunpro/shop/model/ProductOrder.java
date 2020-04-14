package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private String        useCupon;
    private Integer       usePoint;
    private Integer       totalAmount;
    private String        productNames;
    private String        productIds;
    private String        productQuantitys;
    private String        productAmounts;
    private String        productThumbs;
    private Long          accountId;
    private Short         orderState;
    private Integer       receivePoint;

    /* 주문하는 상품의 { id, 수량 } 정보 */
    private Map<Long, Integer> orderInfo   = new HashMap<>();
    /* 주문한 상품의 정보 List */
    private List<Product>      productList = new ArrayList<>();
    /* List 에서 보여지는 하나의 썸네일 주소 { this.productThumbs } 첫번째 id 값을 조회하여 저장됩니다. */
    private String productThumb;

    @Builder
    public ProductOrder(Long id, String ip, LocalDateTime createdDate,
            LocalDateTime modifiedDate, Boolean enabled, String orderName, String orderEmail,
            String orderPhone, String postcode, String addr1, String addr2, String memo,
            Short payment, String productQuantitys, String useCupon, Integer usePoint,
            Integer totalAmount,
            String productIds, Long accountId,
            Map<Long, Integer> orderInfo, Short orderState, Integer receivePoint,
            List<Product> productList) {
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
        this.useCupon = useCupon;
        this.usePoint = usePoint;
        this.totalAmount = totalAmount;
        this.productIds = productIds;
        this.productQuantitys = productQuantitys;
        this.accountId = accountId;
        this.orderInfo = orderInfo;
        this.orderState = orderState;
        this.receivePoint = receivePoint;
        this.productList = productList;
    }

    /*
     * Map 으로 들어있는 { id : quantity } key, value 값을 나눠서 각각
     * productIds, productQuantitys 변수에 담아주기 위해서 사용하는 메소드
     *
     * 해당 메소드를 사용하는 이유는 실제로 서버에 저장되는 정보가 productIds, productQuantitys 으로
     * 구매하는 상품의 정보 그리고 갯수를 각각의 변수에 저장하기 위해서 입니다.
     *
     * productList 변수는 임시변수일뿐 서버에 저장되지는 않습니다.
     */
    public void idsAndQuantitysSet(Map<Long, Integer> orderInfo) {
        StringBuilder idBuffer       = new StringBuilder();
        StringBuilder quantityBuffer = new StringBuilder();

        for (Long id : orderInfo.keySet()) {
            Integer quantity = orderInfo.get(id);

            idBuffer.append(',').append(id);
            quantityBuffer.append(',').append(quantity);
        }

        idBuffer.append(",");
        quantityBuffer.append(',');

        this.productIds = idBuffer.toString();
        this.productQuantitys = quantityBuffer.toString();
    }
}
