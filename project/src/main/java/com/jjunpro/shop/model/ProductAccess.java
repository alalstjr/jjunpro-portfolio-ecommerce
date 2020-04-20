package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ProductAccess {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private Long   accountId;
    private byte   ageRange;
    private String birthday;
    private int    gender;
    private String addr;

    private Long    productId;
    private Integer price;
    private boolean discount;
    private boolean point;

    @Builder
    public ProductAccess(String ip, Long accountId, byte ageRange, String birthday, int gender,
            String addr, Long productId, Integer price, boolean discount, boolean point) {
        this.ip = ip;
        this.accountId = accountId;
        this.ageRange = ageRange;
        this.birthday = birthday;
        this.gender = gender;
        this.addr = addr;
        this.productId = productId;
        this.price = price;
        this.discount = discount;
        this.point = point;
    }
}
