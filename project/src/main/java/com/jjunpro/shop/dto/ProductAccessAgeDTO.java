package com.jjunpro.shop.dto;

import com.jjunpro.shop.model.Product;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductAccessAgeDTO {

    private List<Product> ageTen;
    private List<Product> ageTwenty;
    private List<Product> ageThirty;
    private List<Product> ageForty;
    private List<Product> ageEtc;

    @Builder
    public ProductAccessAgeDTO(List<Product> ageTen,
            List<Product> ageTwenty, List<Product> ageThirty,
            List<Product> ageForty, List<Product> ageEtc) {
        this.ageTen = ageTen;
        this.ageTwenty = ageTwenty;
        this.ageThirty = ageThirty;
        this.ageForty = ageForty;
        this.ageEtc = ageEtc;
    }
}
