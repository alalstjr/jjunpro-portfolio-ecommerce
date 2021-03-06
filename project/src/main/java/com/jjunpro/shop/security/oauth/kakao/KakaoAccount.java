package com.jjunpro.shop.security.oauth.kakao;

import lombok.Data;

@Data
public class KakaoAccount {

    private String email;

    private String age_range;

    private String birthday;

    private String gender;

    private KakaoProfile profile;
}
