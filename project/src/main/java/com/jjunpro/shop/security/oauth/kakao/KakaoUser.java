package com.jjunpro.shop.security.oauth.kakao;

import lombok.Data;

@Data
public class KakaoUser {

    private String id;

    private String connected_at;

    private KakaoAccount kakao_account;
}
