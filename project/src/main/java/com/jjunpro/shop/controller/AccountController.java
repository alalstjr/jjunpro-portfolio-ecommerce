package com.jjunpro.shop.controller;

import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/register")
    public String registration(@ModelAttribute Account account, Model model,
            HttpServletRequest request) {
        Account accountDB = accountService.insertAccount(account);
        securityService.autologin(
                accountDB.getEmail(),
                accountDB.getPassword(),
                accountDB.getUserRole(),
                request
        );

        model.addAttribute("user", accountDB);

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

//    private final UserValidator             userValidator;
//    private final ConnectionFactoryLocator  connectionFactoryLocator;
//    private final UsersConnectionRepository connectionRepository;
//    private final AccountServiceImpl        accountService;
//
//    @InitBinder
//    protected void initBinder(WebDataBinder dataBinder) {
//
//        Object target = dataBinder.getTarget();
//        if (target == null) {
//            return;
//        }
//        System.out.println("Target=" + target);
//
//        if (UserFormDTO.class == target.getClass()) {
//            dataBinder.setValidator(userValidator);
//        }
//    }
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
//    @GetMapping("login")
//    public String login(Model model) {
//        return "account/loginPage";
//    }
//
//    @RequestMapping(value = {"/signin"}, method = RequestMethod.GET)
//    public String signInPage(Model model) {
//        return "redirect:/login";
//    }
//
//    @RequestMapping(value = {"/signup"}, method = RequestMethod.GET)
//    public String signupPage(WebRequest request, Model model) {
//        ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator,
//                connectionRepository);
//        Connection<?> connection  = providerSignInUtils.getConnectionFromSession(request);
//        UserFormDTO   userFormDTO = null;
//
//        if (connection != null) {
//            userFormDTO = new UserFormDTO(connection);
//        } else {
//            userFormDTO = new UserFormDTO();
//        }
//
//        model.addAttribute("userForm", userFormDTO);
//
//        return "account/signupPage";
//    }
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
