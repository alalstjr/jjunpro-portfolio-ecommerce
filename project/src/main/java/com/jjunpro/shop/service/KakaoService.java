package com.jjunpro.shop.service;

import com.jjunpro.shop.OAuth.kakao.KakaoUser;
import java.io.IOException;

public interface KakaoService {

    String login();

    KakaoUser getUserProfile(String code) throws IOException;
}
