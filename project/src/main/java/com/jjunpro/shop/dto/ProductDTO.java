package com.jjunpro.shop.dto;

import com.jjunpro.shop.model.Product;
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
public class ProductDTO {

    private String        ip;
    private Long          id;
    private Boolean       enabled;
    private String        productName;
    private String        explanation;
    private String        tag;
    private String        productType;
    private Boolean       callQuestion;
    private Boolean       cuponEnabled;
    private Boolean       pointEnabled;
    private String        content;
    private String        summaryInfo;
    private Integer       price;
    private Short         discount;
    private Integer       point;
    private Integer       quantity;
    private Integer       buyMinQuantity;
    private Integer       buyMaxQuantity;
    private Boolean       reviewState;
    private LocalDateTime reservationSale;
    private LocalDateTime endSale;
    private Integer       priority;
    private String        shopGroupIds;

    public Product toEntity() {
        return Product.builder()
                .ip(ip)
                .id(id)
                .enabled(enabled)
                .productName(productName)
                .explanation(explanation)
                .tag(tag)
                .productType(productType)
                .callQuestion(callQuestion)
                .cuponEnabled(cuponEnabled)
                .pointEnabled(pointEnabled)
                .content(content)
                .summaryInfo(summaryInfo)
                .price(price)
                .discount(discount)
                .point(point)
                .quantity(quantity)
                .buyMinQuantity(buyMinQuantity)
                .buyMaxQuantity(buyMaxQuantity)
                .reviewState(reviewState)
                .reservationSale(reservationSale)
                .endSale(endSale)
                .priority(priority)
                .shopGroupIds(shopGroupIds)
                .build();
    }
}
