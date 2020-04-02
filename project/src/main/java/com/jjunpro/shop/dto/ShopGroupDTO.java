package com.jjunpro.shop.dto;

import com.jjunpro.shop.model.ShopGroup;
import javax.validation.constraints.NotBlank;
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
public class ShopGroupDTO {

    private Long id;

    private String ip;

    private Boolean enabled;

    @NotBlank(message = "그룹 이름은 필수로 작성해야 합니다.")
    private String shopName;

    private Integer priority;

    private Long parentShopGroupId;

    public ShopGroup toEntity() {
        return ShopGroup.builder()
                .id(id)
                .ip(ip)
                .enabled(enabled)
                .shopName(shopName)
                .priority(priority)
                .parentShopGroupId(parentShopGroupId)
                .build();
    }
}
