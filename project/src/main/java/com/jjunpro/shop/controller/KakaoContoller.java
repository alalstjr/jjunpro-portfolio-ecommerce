package com.jjunpro.shop.controller;

import com.jjunpro.shop.service.KakaoServiceImpl;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class KakaoContoller {

    private final KakaoServiceImpl kakaoService;

    @GetMapping("/login")
    public RedirectView login() {
        RedirectView redirectView = new RedirectView();
        String       url          = kakaoService.login();

        redirectView.setUrl(url);

        return redirectView;
    }

    /* Google Login Call Back 턴 메소드 */
    @GetMapping("")
    public String google(
            @RequestParam("code") String code,
            HttpServletRequest request,
            Model model
    ) throws IOException {
        System.out.println("code");
        kakaoService.getUserProfile(code);

        return "account/userProfile";
    }
}
