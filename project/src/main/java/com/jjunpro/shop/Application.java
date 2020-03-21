package com.jjunpro.shop;

import com.jjunpro.shop.common.CustomBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBanner(new CustomBanner());
        application.run(args);
        // 젠킨슨 테스트
    }
}
