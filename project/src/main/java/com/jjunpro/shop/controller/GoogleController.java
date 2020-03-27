package com.jjunpro.shop.controller;

import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.GoogleServiceImpl;
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
@RequiredArgsConstructor
@RequestMapping("/google")
public class GoogleController {

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
        Person       googleUserProfile = googleService.getUserProfile(code);
        Name         userName          = googleUserProfile.getNames().iterator().next();
        EmailAddress emailAddress      = googleUserProfile.getEmailAddresses().iterator().next();

        /* DB 내부에 사용자가 이미 가입되어 있는지 체크합니다. */
        Optional<Account> accountDB = accountService
                .findByEmail(emailAddress.getValue());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setEmail(emailAddress.getValue());
            accountDB.get().setFirstName(userName.getGivenName());
            accountDB.get().setLastName(userName.getFamilyName());
            userrole = accountDB.get().getUserRole();

            accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .email(emailAddress.getValue())
                    .firstName(userName.getGivenName())
                    .lastName(userName.getFamilyName())
                    .enabled(true)
                    .userRole(UserRole.USER)
                    .build();
            userrole = account.getUserRole();

            accountService.insertAccount(account);

            model.addAttribute("user", account);
        }

        securityService.autologin(
                emailAddress.getValue(),
                null,
                userrole,
                request
        );

        return "account/userProfile";
    }
}
