package com.jjunpro.shop.dto;

import com.jjunpro.shop.model.ProductOrder;
import java.util.HashMap;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductOrderDTO {

    private Long id;

    private String ip;

    private Boolean enabled;

    @NotBlank(message = "주문자 성함은 필수로 작성해야 합니다.")
    private String orderName;

    @Email
    private String orderEmail;

    @NotBlank(message = "연락처는 필수로 작성해야 합니다.")
    private String orderPhone;

    @NotBlank(message = "우편번호는 필수로 작성해야 합니다.")
    private String postcode;

    @NotBlank(message = "주소는 필수로 작성해야 합니다.")
    private String addr1;

    private String addr2;

    private String memo;

    @NotNull(message = "주문결제 방법은 필수로 선택해야 합니다.")
    private Short payment;

    private String useCupon;

    @Min(value = 0, message = "0 포인트 이상만 작성 가능합니다.")
    private Integer usePoint;

    @NotBlank(message = "상품정보는 필수입니다.")
    private String productIds;

    @NotBlank(message = "상품정보는 필수입니다.")
    private String productQuantitys;

    private Integer totalAmount;

    private Integer accountPoint;

    @Builder
    public ProductOrderDTO(Long id, String ip, Boolean enabled, String orderName,
            String orderEmail, String orderPhone, String postcode, String addr1, String addr2,
            String memo, Short payment, String useCupon, Integer usePoint,
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
        this.useCupon = useCupon;
        this.usePoint = usePoint;
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
                .useCupon(useCupon)
                .usePoint(usePoint)
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
            productMap.put(Long.parseLong(productId.trim()), Integer.parseInt(productQuantitysArr[i].trim()));
            i++;
        }

        return productMap;
    }
}
