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
@AllArgsConstructor
@NoArgsConstructor
public class ShopGroup {

    private Long          id;
    private String        ip;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean       enabled;
    private String        name;
    private Integer       viewOrder;
    private Long          parentShopGroupId;
}
