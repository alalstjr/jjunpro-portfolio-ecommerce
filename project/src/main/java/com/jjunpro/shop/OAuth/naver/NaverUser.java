package com.jjunpro.shop.OAuth.naver;

import lombok.Data;

@Data
public class NaverUser {

    private String resultcode;

    private String message;

    private NaverAccount response;
}
