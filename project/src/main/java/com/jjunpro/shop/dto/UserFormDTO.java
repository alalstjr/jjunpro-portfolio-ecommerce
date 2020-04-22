package com.jjunpro.shop.dto;

import com.jjunpro.shop.enums.ColumnType;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.validator.DataMatch;
import com.jjunpro.shop.validator.PasswordMatch;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@PasswordMatch(password = "password", passwordRe = "passwordRe")
public class UserFormDTO {

    private String ip;

    @Email(message = "잘못된 이메일입니다.")
    @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "잘못된 이메일입니다.")
    @NotBlank(message = "이메일은 필수로 작성해야 합니다.")
    @DataMatch(message = "이미 존재하는 이메일 입니다.", column = ColumnType.EMAIL)
    private String email;

    @NotBlank(message = "비밀번호는 필수로 작성해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호를 다시한번 입력해 주세요.")
    private String passwordRe;

    @NotBlank(message = "이름은 필수로 작성해야 합니다.")
    private String username;

    @NotBlank(message = "우편번호는 필수로 작성해야 합니다.")
    private String postcode;

    @NotBlank(message = "주소는 필수로 작성해야 합니다.")
    private String addr1;

    private String addr2;

    private String phoneNumber;

    @Pattern(regexp = "(?:19|20)[0-9]{2}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-9])|(?:(?!02)(?:0[1-9]|1[0-2])(?:30))|(?:(?:0[13578]|1[02])-31))", message = "옳바른 생년월일을 작성해 주세요.")
    private String birthday;

    private int gender;

    @NotNull
    @AssertTrue(message = "이용약관 동의를 체크해 주세요.")
    private boolean agree1;

    @NotNull
    @AssertTrue(message = "개인정보 수집 및 이용 동의를 체크해 주세요.")
    private Boolean agree2;

    private Boolean agree3;

    private Boolean agree4;

    public Account toEntity() {
        return Account.builder()
                .ip(ip)
                .email(email)
                .password(password)
                .username(username)
                .postcode(postcode)
                .addr1(addr1)
                .addr2(addr2)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .gender(gender)
                .build();
    }
}
