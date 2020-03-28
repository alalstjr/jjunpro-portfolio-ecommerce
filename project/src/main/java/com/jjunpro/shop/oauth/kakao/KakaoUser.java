package com.jjunpro.shop.oauth.kakao;

import lombok.Data;

@Data
public class KakaoUser {

    private String id;

    private String connected_at;

    private KakaoAccount kakao_account;
}
