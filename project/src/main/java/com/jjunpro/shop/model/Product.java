package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
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
    private Short         point;
    private Integer       quantity;
    private Integer       buyMinQuantity;
    private Integer       buyMaxQuantity;
    private Boolean       reviewState;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationSale;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endSale;

    private Integer priority;
    private String  shopGroupIds;
    private String  fileStorageIds;

    /* Storage Value */

    /* shopGroupIds 조회하여 List 를 저장하는 임시변수 */
    private Set<ShopGroup> shopGroupList = new HashSet<>();

    private MultipartFile[] fileStorage;

    /* 파일삭제 id 값을 저장하는 임시변수 */
    private String deleteFileStorageIds;

    /* 목록에서 보여지는 썸네일 이미지 파일 */
    private String thumbnail;

    @Builder
    public Product(Long id, String ip, LocalDateTime createdDate, LocalDateTime modifiedDate,
            Boolean enabled, String productName, String explanation, String tag,
            String productType, Boolean callQuestion, Boolean cuponEnabled,
            Boolean pointEnabled, String content, String summaryInfo, Integer price,
            Short discount, Short point, Integer quantity, Integer buyMinQuantity,
            Integer buyMaxQuantity, Boolean reviewState, LocalDateTime reservationSale,
            LocalDateTime endSale, Integer priority, String shopGroupIds,
            String fileStorageIds, Set<ShopGroup> shopGroupList,
            MultipartFile[] fileStorage) {
        this.id = id;
        this.ip = ip;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.enabled = enabled;
        this.productName = productName;
        this.explanation = explanation;
        this.tag = tag;
        this.productType = productType;
        this.callQuestion = callQuestion;
        this.cuponEnabled = cuponEnabled;
        this.pointEnabled = pointEnabled;
        this.content = content;
        this.summaryInfo = summaryInfo;
        this.price = price;
        this.discount = discount;
        this.point = point;
        this.quantity = quantity;
        this.buyMinQuantity = buyMinQuantity;
        this.buyMaxQuantity = buyMaxQuantity;
        this.reviewState = reviewState;
        this.reservationSale = reservationSale;
        this.endSale = endSale;
        this.priority = priority;
        this.shopGroupIds = shopGroupIds;
        this.fileStorageIds = fileStorageIds;
        this.shopGroupList = shopGroupList;
        this.fileStorage = fileStorage;
    }
}
