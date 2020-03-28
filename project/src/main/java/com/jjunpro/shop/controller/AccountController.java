package com.jjunpro.shop.controller;

import com.jjunpro.shop.dto.UserFormDTO;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
@Transactional
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl  accountService;
    private final SecurityServiceImpl securityService;

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("account", new Account());
        return "main/main";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/join")
    public String join() {
        return "account/join";
    }

    @PostMapping("/join")
    public String postJoin(Model model,
            HttpServletRequest request) {
//        Account accountDB = accountService.insertAccount(account);
//        securityService.autologin(
//                accountDB.getEmail(),
//                accountDB.getPassword(),
//                accountDB.getUserRole(),
//                request
//        );
//
//        model.addAttribute("user", accountDB);

        return "account/userprofile";
    }

//    private final UserValidator             userValidator;
//    private final ConnectionFactoryLocator  connectionFactoryLocator;
//    private final UsersConnectionRepository connectionRepository;
//    private final AccountServiceImpl        accountService;
//
//
//    @GetMapping(value = {"/", "/welcome"})
//    public String welcomePage(Model model) {
//        model.addAttribute("message", "This is welcome page!");
//        return "main/main";
//    }
//
//    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
//    public String userInfo(Model model, Principal principal) {
////        String username = principal.getName();
////        System.out.println("User Name: " + username);
////        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
//        model.addAttribute("userInfo", "loginedUser.getUsername()");
//        return "account/userInfoPage";
//    }
//
//
//    @RequestMapping(value = {"/signin"}, method = RequestMethod.GET)
//    public String signInPage(Model model) {
//        return "redirect:/login";
//    }
//
//
//    @RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
//    public String signupSave(
//            WebRequest request,
//            Model model,
//            @ModelAttribute("userForm") @Validated UserFormDTO userFormDTO,
//            BindingResult result,
//            final RedirectAttributes redirectAttributes
//    ) {
//
//        // Validation error.
//        if (result.hasErrors()) {
//            return "account/signupPage";
//        }
//
//        Account registered = null;
//
//        try {
//            registered = accountService.registerCreateAccount(userFormDTO);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            model.addAttribute("errorMessage", "Error " + ex.getMessage());
//            return "account/signupPage";
//        }
//
//        if (userFormDTO.getSignInProvider() != null) {
//            ProviderSignInUtils providerSignInUtils //
//                    = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
//            providerSignInUtils.doPostSignUp(registered.getUsername(), request);
//        }
//
//        return "redirect:/userInfo";
//    }
}
