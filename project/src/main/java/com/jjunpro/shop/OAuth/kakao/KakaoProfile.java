package com.jjunpro.shop.OAuth.kakao;

import lombok.Data;

@Data
public class KakaoProfile {

    private String nickname;

    private String thumbnail_image_url;

    private String profile_image_url;
}
