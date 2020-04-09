package com.jjunpro.shop.dto;

import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.validator.UserDataMatch;
import com.jjunpro.shop.validator.UserExistence;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@UserExistence(id = "id")
@UserDataMatch(id = "id", domain = DomainType.PRODUCTORDER)
public class ReceiptDTO {

    private Long id;

    /* ReceiptConverter.class 인스턴스 시킬때 필요한 생성자 */
    public ReceiptDTO(Long id) {
        this.id = id;
    }
}
