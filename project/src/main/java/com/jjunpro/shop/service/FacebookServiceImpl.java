package com.jjunpro.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:application-social.properties")
public class FacebookServiceImpl implements FacebookService {

    @Value("${default.facebook.uri}")
    private String uri;

    @Value("${spring.social.facebook.app-id}")
    private String facebookId;

    @Value("${spring.social.facebook.app-secret}")
    private String facebookSecret;

    /* FaceBook 관리자 접근 권한 생성 */
    private FacebookConnectionFactory facebookConnectionFactory() {
        return new FacebookConnectionFactory(this.facebookId, this.facebookSecret);
    }

    /*
     * MyServer 에서 FacebookServer 로 OAuth 인증 로그인 정보를 담아서 보냅니다.
     *
     * FaceBook 로그인 콜백 URI 주소, 원하는 정보를 접근 권한에 담아서 전송
     * 콜백 주소를 전달받아 this.uri 링크를 RedirectView 전송
     *
     * MyServer      -> FacebookServer
     * facebooklogin -> getFacebookAccessToken
     * */
    @Override
    public String facebookLogin() {
        OAuth2Parameters parameters = new OAuth2Parameters();

        parameters.setRedirectUri(this.uri);
        parameters.setScope("public_profile,email");

        return facebookConnectionFactory()
                .getOAuthOperations()
                .buildAuthenticateUrl(parameters);
    }

    /*
     * MyServer 에서 보내는 OAuth 인증이 맞는지 FacebookServer 에 인증 검토를 보내고 AccessToken 을 받아옵니다.
     *
     * MyServer               -> FacebookServer
     * getFacebookAccessToken -> getFacebookUserProfile
     * */
    @Override
    public String getFacebookAccessToken(String code) {
        return facebookConnectionFactory()
                .getOAuthOperations()
                .exchangeForAccess(code, this.uri, null)
                .getAccessToken();
    }

    /*
     * FacebookServer 에서 OAuth 인증이 완료된 경우 사용자의 정보를 받아옵니다.
     *
     * FacebookServer         -> MyServer
     * getFacebookAccessToken -> getFacebookUserProfile
     * */
    @Override
    public User getFacebookUserProfile(String accessToken) {
        String[]         fields           = {"id", "first_name", "last_name", "email"};
        FacebookTemplate facebookTemplate = new FacebookTemplate(accessToken);

        return facebookTemplate
                .fetchObject("me", User.class, fields);
    }
}
