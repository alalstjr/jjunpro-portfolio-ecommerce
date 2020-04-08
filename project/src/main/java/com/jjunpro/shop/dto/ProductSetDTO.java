package com.jjunpro.shop.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSetDTO {

    @NotNull(message = "상품 정보는 필수입니다.")
    private Long    id;

    @NotNull(message = "상품 수량은 필수로 작성해야 합니다.")
    private Integer quantity = 1;
}
