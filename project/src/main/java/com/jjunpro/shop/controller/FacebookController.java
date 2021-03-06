package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;

import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.FacebookServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/facebook")
public class FacebookController {

    private final IpUtil              ipUtil;
    private final FacebookServiceImpl facebookService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/login")
    public RedirectView login() {
        RedirectView redirectView = new RedirectView();
        String       url          = this.facebookService.login();

        redirectView.setUrl(url);

        return redirectView;
    }

    @GetMapping("")
    public String facebook(@RequestParam("code") String code) {
        String accessToken = this.facebookService.getAccessToken(code);

        return "redirect:/facebook/profileData/" + accessToken;
    }

    @GetMapping("/profileData/{accessToken:.+}")
    public String profileData(
            @PathVariable String accessToken,
            Model model,
            HttpServletRequest request
    ) {
        /* 페이스북에서 받아온 사용자의 정보 */
        User   userProfile = this.facebookService.getUserProfile(accessToken);
        String birthday    = null;
        String ageRange    = null;
        int    gender      = 0;

        /* 사용자의 생일정보가 존재하는 경우 */
        if (userProfile.getBirthday() != null) {
            birthday = userProfile.getBirthday();
        }
        /* 사용자의 나이정보가 존재하는 경우 */
        if (userProfile.getAgeRange() != null) {
            ageRange = userProfile.getAgeRange().toString();
        }
        /* 사용자의 성별정보가 존재하는 경우 */
        if (userProfile.getGender() != null) {
            gender = userProfile.getGender().equals("male") ? 1
                    : userProfile.getGender().equals("female") ? 2 : 0;
        }

        /* DB 내부에 사용자가 이미 가입되어 있는지 체크합니다. */
        Optional<Account> accountDB = this.accountService.findByEmail(userProfile.getEmail());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setIp(ipUtil.getUserIp(request));
            accountDB.get().setEmail(userProfile.getEmail());
            accountDB.get().setUsername(userProfile.getFirstName() + userProfile.getLastName());
            accountDB.get().setBirthday(birthday);
            accountDB.get().setAgeRange(ageRange);

            accountDB.get().setGender(gender);
            userrole = accountDB.get().getUserRole();

            this.accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .ip(ipUtil.getUserIp(request))
                    .email(userProfile.getEmail())
                    .username(userProfile.getFirstName() + userProfile.getLastName())
                    .enabled(true)
                    .userRole(UserRole.USER)
                    .birthday(birthday)
                    .ageRange(ageRange)
                    .gender(gender)
                    .point(10000)
                    .build();
            userrole = account.getUserRole();

            this.accountService.insertAccount(account);

            model.addAttribute("user", account);
        }

        this.securityService.autologin(
                userProfile.getEmail(),
                null,
                userrole,
                request
        );

        /* 로그인 후 이전페이지로 이동합니다. */
        HttpSession session  = request.getSession();
        String      prevPage = (String) session.getAttribute("prevPage");

        return "redirect:" + prevPage;
    }
}
