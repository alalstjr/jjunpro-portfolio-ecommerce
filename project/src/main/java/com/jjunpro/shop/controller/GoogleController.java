package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;

import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.GoogleServiceImpl;
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
@RequestMapping("/google")
public class GoogleController {

    private final IpUtil              ipUtil;
    private final GoogleServiceImpl   googleService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/login")
    public RedirectView login() {
        RedirectView redirectView = new RedirectView();
        String       url          = googleService.login();

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
        /* 구글에서 받아온 사용자의 정보 */
        Person       userProfile  = this.googleService.getUserProfile(code);
        Name         userName     = userProfile.getNames().iterator().next();
        EmailAddress emailAddress = userProfile.getEmailAddresses().iterator().next();
        String       ageRange     = null;
        int          gender       = 0;

        /**
         * Ex) JSON CODE
         *
         * "birthdays":[
         *  {
         *      "date":{
         *          "day":21,
         *          "month":7,
         *          "year":1994
         *      },
         *      "metadata":{
         *          "primary":true,
         *              "source":{
         *                  "id":"100376147650509948248",
         *                  "type":"PROFILE"
         *              }
         *          }
         *      }
         *      ...
         * ]
         *
         * 나누어져 있는 날짜를 합쳐서 저장힙니다.
         * */
        StringBuilder birthday = new StringBuilder();
        if (userProfile.getBirthdays() != null) {
            birthday.append(userProfile.getBirthdays().iterator().next().getDate().getYear());
            birthday.append(userProfile.getBirthdays().iterator().next().getDate().getMonth());
            birthday.append(userProfile.getBirthdays().iterator().next().getDate().getDay());
        }

        /* 사용자의 나이정보가 존재하는 경우 */
        if (userProfile.getAgeRange() != null) {
            ageRange = userProfile.getAgeRange();
        }

        /* DB 내부에 사용자가 이미 가입되어 있는지 체크합니다. */
        Optional<Account> accountDB = this.accountService
                .findByEmail(emailAddress.getValue());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setIp(ipUtil.getUserIp(request));
            accountDB.get().setEmail(emailAddress.getValue());
            accountDB.get().setUsername(userName.getGivenName() + userName.getFamilyName());
            accountDB.get().setAgeRange(ageRange);
            accountDB.get().setGender(gender);
            accountDB.get().setBirthday(birthday.toString());
            userrole = accountDB.get().getUserRole();

            this.accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .ip(ipUtil.getUserIp(request))
                    .email(emailAddress.getValue())
                    .username(userName.getGivenName() + userName.getFamilyName())
                    .enabled(true)
                    .userRole(UserRole.USER)
                    .ageRange(ageRange)
                    .gender(gender)
                    .birthday(birthday.toString())
                    .point(10000)
                    .build();
            userrole = account.getUserRole();

            this.accountService.insertAccount(account);

            model.addAttribute("user", account);
        }

        securityService.autologin(
                emailAddress.getValue(),
                null,
                userrole,
                request
        );

        return "redirect:/";
    }
}
