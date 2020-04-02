package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean       enabled;
    private byte          deliveryName;
    private byte          deliveryType;
    private byte          deliveryPayType;
    private byte          deliveryDefaultPay;
    private byte          deliveryIfPay;
    private byte          deliveryQuantityPay;
}
