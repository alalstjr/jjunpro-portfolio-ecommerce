package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;

import com.jjunpro.shop.security.oauth.kakao.KakaoAccount;
import com.jjunpro.shop.security.oauth.kakao.KakaoUser;
import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.KakaoServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import com.jjunpro.shop.util.IpUtil;
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

    private final IpUtil              ipUtil;
    private final KakaoServiceImpl    kakaoService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/login")
    public RedirectView login() {
        RedirectView redirectView = new RedirectView();
        String       url          = this.kakaoService.login();

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
        KakaoUser    userProfile  = this.kakaoService.getUserProfile(code);
        KakaoAccount kakaoAccount = userProfile.getKakao_account();
        String       birthday     = null;
        String       ageRange     = null;
        int          gender       = 0;

        /* 사용자의 생일정보가 존재하는 경우 */
        if (kakaoAccount.getBirthday() != null) {
            birthday = kakaoAccount.getBirthday();
        }
        /* 사용자의 나이정보가 존재하는 경우 */
        if (kakaoAccount.getAge_range() != null) {
            ageRange = kakaoAccount.getAge_range();
        }
        /* 사용자의 성별정보가 존재하는 경우 */
        if (kakaoAccount.getGender() != null) {
            gender = kakaoAccount.getGender().equals("male") ? 1
                    : kakaoAccount.getGender().equals("female") ? 2 : 0;
        }

        /* DB 내부에 사용자가 이미 가입되어 있는지 체크합니다. */
        Optional<Account> accountDB = this.accountService
                .findByEmail(kakaoAccount.getEmail());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setIp(ipUtil.getUserIp(request));
            accountDB.get().setEmail(kakaoAccount.getEmail());
            accountDB.get().setUsername(kakaoAccount.getProfile().getNickname());
            accountDB.get().setAgeRange(ageRange);
            accountDB.get().setBirthday(birthday);
            accountDB.get().setGender(gender);
            userrole = accountDB.get().getUserRole();

            this.accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .ip(ipUtil.getUserIp(request))
                    .email(kakaoAccount.getEmail())
                    .username(kakaoAccount.getProfile().getNickname())
                    .enabled(true)
                    .userRole(UserRole.USER)
                    .ageRange(ageRange)
                    .birthday(birthday)
                    .gender(gender)
                    .point(10000)
                    .build();
            userrole = account.getUserRole();

            this.accountService.insertAccount(account);

            model.addAttribute("user", account);
        }

        this.securityService.autologin(
                kakaoAccount.getEmail(),
                null,
                userrole,
                request
        );

        return "redirect:/";
    }
}
