package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;

import com.jjunpro.shop.security.oauth.naver.NaverAccount;
import com.jjunpro.shop.security.oauth.naver.NaverUser;
import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.NaverServiceImpl;
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
@RequiredArgsConstructor
@RequestMapping("/naver")
public class NaverController {

    private final IpUtil              ipUtil;
    private final NaverServiceImpl    naverService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/login")
    public RedirectView login() {
        RedirectView redirectView = new RedirectView();
        String       url          = this.naverService.login();

        redirectView.setUrl(url);

        return redirectView;
    }

    /* Naver Login Call Back 턴 메소드 */
    @GetMapping("")
    public String naver(
            @RequestParam("code") String code,
            HttpServletRequest request,
            Model model
    ) throws IOException {
        /* 네이버에서 받아온 사용자의 정보 */
        NaverUser    userProfile  = this.naverService.getUserProfile(code);
        NaverAccount naverAccount = userProfile.getResponse();
        String       birthday     = null;
        String       ageRange     = null;
        int          gender       = 0;

        /* 사용자의 생일정보가 존재하는 경우 */
        if (naverAccount.getBirthday() != null) {
            birthday = naverAccount.getBirthday();
        }
        /* 사용자의 나이정보가 존재하는 경우 */
        if (naverAccount.getAge() != null) {
            ageRange = naverAccount.getAge();
        }
        /* 사용자의 성별정보가 존재하는 경우 */
        if (naverAccount.getGender() != null) {
            gender = naverAccount.getGender().equals("M") ? 1
                    : naverAccount.getGender().equals("F") ? 2 : 0;
        }

        /* DB 내부에 사용자가 이미 가입되어 있는지 체크합니다. */
        Optional<Account> accountDB = this.accountService
                .findByEmail(naverAccount.getEmail());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setIp(ipUtil.getUserIp(request));
            accountDB.get().setEmail(naverAccount.getEmail());
            accountDB.get().setUsername(naverAccount.getName());
            accountDB.get().setAgeRange(ageRange);
            accountDB.get().setBirthday(birthday);
            accountDB.get().setGender(gender);
            userrole = accountDB.get().getUserRole();

            this.accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .ip(ipUtil.getUserIp(request))
                    .email(naverAccount.getEmail())
                    .username(naverAccount.getName())
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
                naverAccount.getEmail(),
                null,
                userrole,
                request
        );

        return "redirect:/";
    }
}
