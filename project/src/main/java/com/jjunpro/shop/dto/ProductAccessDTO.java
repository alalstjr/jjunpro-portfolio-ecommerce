package com.jjunpro.shop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductAccessDTO {

    private String ageTen;
    private String ageTwenty;
    private String ageThirty;
    private String ageForty;
    private String ageEtc;

    @Builder
    public ProductAccessDTO(String ageTen, String ageTwenty, String ageThirty,
            String ageForty, String ageEtc) {
        this.ageTen = ageTen;
        this.ageTwenty = ageTwenty;
        this.ageThirty = ageThirty;
        this.ageForty = ageForty;
        this.ageEtc = ageEtc;
    }
}
