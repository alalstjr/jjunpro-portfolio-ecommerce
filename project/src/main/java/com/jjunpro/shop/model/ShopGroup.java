package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
public class ShopGroup {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Boolean       enabled;
    private String        shopName;
    private Integer       priority;
    private Long          parentShopGroupId;

    /* Storage Value */

    /* parentShopGroupId 조회하여 List 를 저장하는 임시변수 */
    private Set<ShopGroup> childrenShopGroupList = new HashSet<>();
}

