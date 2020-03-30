package com.jjunpro.shop.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindByEmailDTO {

    @NotBlank(message = "이름은 필수로 작성해야 합니다.")
    private String username;

    @NotBlank(message = "전화번호는 필수로 작성해야 합니다.")
    private String phoneNumber;
}
