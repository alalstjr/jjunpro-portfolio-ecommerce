package com.jjunpro.shop.controller;

import com.jjunpro.shop.OAuth.kakao.KakaoAccount;
import com.jjunpro.shop.OAuth.kakao.KakaoUser;
import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.KakaoServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import java.io.IOException;
import java.util.Optional;
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

    private final KakaoServiceImpl    kakaoService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/login")
    public RedirectView login() {
        RedirectView redirectView = new RedirectView();
        String       url          = kakaoService.login();

        redirectView.setUrl(url);

        return redirectView;
    }

    /* Kakao Login Call Back 턴 메소드 */
    @GetMapping("")
    public String kakao(
            @RequestParam("code") String code,
            HttpServletRequest request,
            Model model
    ) throws IOException {
        /* 카카오에서 받아온 사용자의 정보 */
        KakaoUser userProfile = kakaoService.getUserProfile(code);
        KakaoAccount kakaoAccount = userProfile.getKakao_account();

        /* DB 내부에 사용자가 이미 가입되어 있는지 체크합니다. */
        Optional<Account> accountDB = accountService
                .findByEmail(kakaoAccount.getEmail());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setEmail(kakaoAccount.getEmail());
            accountDB.get().setFirstName(kakaoAccount.getProfile().getNickname());
            accountDB.get().setAgeRange(kakaoAccount.getAge_range());
            accountDB.get().setBirthday(kakaoAccount.getBirthday());
            accountDB.get().setGender(kakaoAccount.getGender());
            userrole = accountDB.get().getUserRole();

            accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .email(kakaoAccount.getEmail())
                    .firstName(kakaoAccount.getProfile().getNickname())
                    .enabled(true)
                    .userRole(UserRole.USER)
                    .ageRange(kakaoAccount.getAge_range())
                    .birthday(kakaoAccount.getBirthday())
                    .gender(kakaoAccount.getGender())
                    .build();
            userrole = account.getUserRole();

            accountService.insertAccount(account);

            model.addAttribute("user", account);
        }

        securityService.autologin(
                kakaoAccount.getEmail(),
                null,
                userrole,
                request
        );

        return "account/userProfile";
    }
}
