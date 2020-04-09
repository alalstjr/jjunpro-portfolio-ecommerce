package com.jjunpro.shop.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class ProductSetDTO {

    @NotNull(message = "상품 정보는 필수입니다.")
    private String id;

    @NotBlank(message = "상품 수량은 필수로 작성해야 합니다.")
    private String quantity;
}