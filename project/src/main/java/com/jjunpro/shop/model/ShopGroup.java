package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
    private List<ShopGroup> childrenShopGroupList = new ArrayList<>();

    private Integer productCount;

    @Builder
    public ShopGroup(Long id, String ip, LocalDateTime createdDate,
            LocalDateTime modifiedDate, Boolean enabled, String shopName, Integer priority,
            Long parentShopGroupId,
            List<ShopGroup> childrenShopGroupList) {
        this.id = id;
        this.ip = ip;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.enabled = enabled;
        this.shopName = shopName;
        this.priority = priority;
        this.parentShopGroupId = parentShopGroupId;
        this.childrenShopGroupList = childrenShopGroupList;
    }
}

