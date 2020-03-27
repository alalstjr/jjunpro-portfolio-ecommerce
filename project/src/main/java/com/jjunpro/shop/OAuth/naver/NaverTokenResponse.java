package com.jjunpro.shop.OAuth.naver;

import com.google.api.client.util.Key;
import lombok.Data;

@Data
public class NaverTokenResponse {

    @Key("access_token")
    private String accessToken;

    @Key("refresh_token")
    private String refreshToken;

    @Key("token_type")
    private String tokenType;

    @Key("expires_in")
    private String expiresIn;
}
