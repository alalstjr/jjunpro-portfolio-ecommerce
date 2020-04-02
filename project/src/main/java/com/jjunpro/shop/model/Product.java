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
public class Product {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean       enabled;
    private String        productName;
    private String        explanation;
    private String        tag;
    private String        productType;
    private Boolean       callQuestion;
    private Boolean       cuponEnabled;
    private Boolean       pointEnabled;
    private String        contentPc;
    private String        contentM;
    private String        summaryInfo;
    private Integer       price;
    private Short         discount;
    private Short         pointType;
    private Integer       point;
    private Integer       quantity;
    private Integer       buyMinQuantity;
    private Integer       buyMaxQuantity;
    private Boolean       reviewState;
    private LocalDateTime reservationSale;
    private LocalDateTime endSale;
    private Integer       priority;
    private String        shopGroupIds;
}
