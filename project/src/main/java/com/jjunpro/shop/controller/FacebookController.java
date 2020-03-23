package com.jjunpro.shop.controller;

import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.FacebookServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class FacebookController {

    private final FacebookServiceImpl facebookService;
    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/facebookLogin")
    public RedirectView facebooklogin() {
        RedirectView redirectView = new RedirectView();
        String       url          = facebookService.facebooklogin();

        System.out.println(url);
        redirectView.setUrl(url);

        return redirectView;
    }

    @GetMapping("/facebook")
    public String facebook(@RequestParam("code") String code) {
        String accessToken = facebookService.getFacebookAccessToken(code);

        return "redirect:/facebookprofiledata/" + accessToken;
    }

    @GetMapping("/facebookprofiledata/{accessToken:.+}")
    public String facebookprofiledata(
            @PathVariable String accessToken,
            Model model,
            HttpServletRequest request
    ) {
        User facebookUserProfile = facebookService.getFacebookUserProfile(accessToken);

        Optional<Account> accountDB = accountService.findByEmail(facebookUserProfile.getEmail());

        UserRole userrole;

        if (accountDB.isPresent()) {
            accountDB.get().setFirstName(facebookUserProfile.getFirstName());
            accountDB.get().setLastName(facebookUserProfile.getLastName());
            userrole = accountDB.get().getUserRole();

            accountService.updateAccount(accountDB.get());

            model.addAttribute("user", accountDB.get());
        } else {
            Account account = Account.builder()
                    .firstName(facebookUserProfile.getFirstName())
                    .lastName(facebookUserProfile.getLastName())
                    .enabled(true)
                    .userRole(UserRole.USER)
                    .build();
            userrole = account.getUserRole();

            accountService.insertAccount(account);

            model.addAttribute("user", account);
        }

        securityService.autologin(
                facebookUserProfile.getEmail(),
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

        return "account/userprofile";
    }
}
