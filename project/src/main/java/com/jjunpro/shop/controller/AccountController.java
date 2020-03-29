package com.jjunpro.shop.controller;

import com.jjunpro.shop.dto.UserFormDTO;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import com.jjunpro.shop.validator.aspect.BindValidator;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
@Transactional
@RequiredArgsConstructor
public class AccountController {

    private static final String VIEWS_ACCOUNT_CREATE_OR_UPDATE_FORM = "account/join";

    private final AccountServiceImpl accountService;

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
    public String join(ModelMap model) {
        UserFormDTO userFormDTO = new UserFormDTO();
        model.addAttribute("userFormDTO", userFormDTO);

        return VIEWS_ACCOUNT_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/join")
    public String postJoin(
            @Valid @ModelAttribute UserFormDTO userFormDTO,
            BindingResult bindingResult,
            ModelMap model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userFormDTO", userFormDTO);

            return VIEWS_ACCOUNT_CREATE_OR_UPDATE_FORM;
        }

        accountService.insertAccount(userFormDTO.toEntity());

        return "account/login";
    }
}
