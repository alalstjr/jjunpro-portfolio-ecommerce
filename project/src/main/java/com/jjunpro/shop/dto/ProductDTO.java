package com.jjunpro.shop.dto;

import com.jjunpro.shop.model.Product;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String ip;

    private Long id;

    private Boolean enabled;

    @NotBlank(message = "상품 이름은 필수로 작성해야 합니다.")
    private String productName;

    private String explanation;

    private String tag;

    private String productType;

    private Boolean callQuestion;

    private Boolean cuponEnabled;

    private Boolean pointEnabled;

    private String content;

    private String summaryInfo;

    @Min(value = 0, message = "0 이하로 작성할 수 없습니다.")
    @NotNull(message = "상품 가격은 필수로 작성해야 합니다.")
    private Integer price;

    private Short discount;

    private Integer point;

    @Min(value = 0, message = "0 이하로 작성할 수 없습니다.")
    @NotNull(message = "상품 수량 필수로 작성해야 합니다.")
    private Integer quantity;

    @Min(value = 0, message = "0 이하로 작성할 수 없습니다.")
    @NotNull(message = "최소상품 구입수량은 필수로 작성해야 합니다.")
    private Integer buyMinQuantity = 1;

    @Min(value = 0, message = "0 이하로 작성할 수 없습니다.")
    @NotNull(message = "최대상품 구입수량은 필수로 작성해야 합니다.")
    private Integer buyMaxQuantity = 9999;

    private Boolean reviewState;

    private String reservationSale;

    private String endSale;

    private Integer priority;

    @NotBlank(message = "분류 지정은 필수로 작성해야 합니다.")
    private String shopGroupIds;

    private MultipartFile[] fileStorage;

    private String fileStorageIds;

    private String deleteFileStorageIds;

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
                .reservationSale(this.defaultDate(reservationSale))
                .endSale(this.defaultDate(endSale))
                .priority(priority)
                .shopGroupIds(shopGroupIds)
                .fileStorage(fileStorage)
                .fileStorageIds(fileStorageIds)
                .build();
    }

    public LocalDateTime defaultDate(String date) {
        if (!date.isEmpty()) {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        return null;
    }
}
