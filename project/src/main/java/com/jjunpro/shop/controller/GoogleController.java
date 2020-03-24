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
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleServiceImpl   googleService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/googleLogin")
    public RedirectView googleLogin() {

        RedirectView redirectView = new RedirectView();
        String       url          = googleService.googleLogin();
        redirectView.setUrl(url);

        return redirectView;
    }

    /* Google Login Call Back 턴 메소드 */
    @GetMapping("/google")
    public String google(
            @RequestParam("code") String code,
            HttpServletRequest request,
            Model model
    ) throws IOException {
        Person       googleUserProfile = googleService.getGoogleUserProfile(code);
        Name         userName          = googleUserProfile.getNames().iterator().next();
        EmailAddress emailAddress      = googleUserProfile.getEmailAddresses().iterator().next();

        Optional<Account> accountDB = accountService
                .findByEmail(emailAddress.getValue());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setFirstName(userName.getGivenName());
            accountDB.get().setLastName(userName.getFamilyName());
            userrole = accountDB.get().getUserRole();

            accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
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

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println(name);

        return "account/userProfile";
    }
}
