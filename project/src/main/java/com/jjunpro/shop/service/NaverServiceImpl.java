package com.jjunpro.shop.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonString;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.model.Person;
import com.jjunpro.shop.OAuth.kakao.KakaoAuthorizationCodeTokenRequest;
import com.jjunpro.shop.OAuth.kakao.KakaoTemplate;
import com.jjunpro.shop.OAuth.kakao.KakaoUser;
import com.jjunpro.shop.OAuth.naver.NaverAuthorizationCodeTokenRequest;
import com.jjunpro.shop.OAuth.naver.NaverBrowserClientRequestUrl;
import com.jjunpro.shop.OAuth.naver.NaverTemplate;
import com.jjunpro.shop.OAuth.naver.NaverTokenResponse;
import com.jjunpro.shop.OAuth.naver.NaverUser;
import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NaverServiceImpl implements NaverService {

    @Value("${default.naver.uri}")
    private String uri;

    @Value("${spring.social.naver.app-id}")
    private String appId;

    @Value("${spring.social.naver.app-secret}")
    private String appSecret;

    @Override
    public String login() {
        return new NaverBrowserClientRequestUrl(this.appId, this.uri, null)
                .setResponseTypes(Collections.singleton("code"))
                .build();
    }

    @Override
    public NaverUser getUserProfile(String code) throws IOException {

        HttpTransport  httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory   = new JacksonFactory();

        NaverTokenResponse tokenResponse =
                new NaverAuthorizationCodeTokenRequest(
                        httpTransport,
                        jsonFactory,
                        this.appId,
                        this.appSecret,
                        code,
                        this.uri
                ).executeNaver();

        NaverTemplate naverTemplate = new NaverTemplate(tokenResponse.getAccessToken());

        return naverTemplate.fetchObject(NaverUser.class);
    }
}
