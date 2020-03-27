package com.jjunpro.shop.service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.model.Person;
import com.jjunpro.shop.OAuth.kakao.KakaoAuthorizationCodeTokenRequest;
import com.jjunpro.shop.OAuth.kakao.KakaoBrowserClientRequestUrl;
import com.jjunpro.shop.OAuth.kakao.KakaoTemplate;
import com.jjunpro.shop.OAuth.kakao.KakaoUser;
import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KakaoServiceImpl implements KakaoService {

    @Value("${default.kakao.uri}")
    private String uri;

    @Value("${spring.social.kakao.app-id}")
    private String kakaoId;

    @Value("${spring.social.kakao.app-secret}")
    private String kakaoSecret;

    @Override
    public String login() {
        return new KakaoBrowserClientRequestUrl(this.kakaoId, this.uri, null)
                .setResponseTypes(Collections.singleton("code"))
                .build();
    }

    @Override
    public Person getUserProfile(String code) throws IOException {
        HttpTransport  httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory   = new JacksonFactory();

        TokenResponse tokenResponse =
                new KakaoAuthorizationCodeTokenRequest(
                        httpTransport,
                        jsonFactory,
                        this.kakaoId,
                        this.kakaoSecret,
                        code,
                        this.uri
                ).execute();

        System.out.println("=========");
        System.out.println(tokenResponse);

        KakaoTemplate kakaoTemplate = new KakaoTemplate(tokenResponse.getAccessToken());
        KakaoUser     user          = kakaoTemplate.fetchObject(KakaoUser.class);

        System.out.println("=========");
        System.out.println(user.getKakao_account().getEmail());

        return null;
    }
}
