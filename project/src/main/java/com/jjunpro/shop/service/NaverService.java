package com.jjunpro.shop.service;

import com.jjunpro.shop.security.oauth.naver.NaverUser;
import java.io.IOException;

public interface NaverService {

    String login();

    NaverUser getUserProfile(String code) throws IOException;
}
