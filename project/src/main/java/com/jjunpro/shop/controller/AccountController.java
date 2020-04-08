package com.jjunpro.shop.controller;

import com.jjunpro.shop.dto.FindByEmailDTO;
import com.jjunpro.shop.dto.UserFormDTO;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Transactional
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;
    private final IpUtil             ipUtil;

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    /*
     * 회원가입
     * */
    @GetMapping("/join")
    public String join(ModelMap model) {
        UserFormDTO userFormDTO = new UserFormDTO();
        model.addAttribute("userFormDTO", userFormDTO);

        return "account/join";
    }

    @PostMapping("/join")
    public String postJoin(
            HttpServletRequest request,
            @Valid UserFormDTO userFormDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userFormDTO", userFormDTO);

            return "account/join";
        }

        /* 사용자의 IP 를 등록합니다. */
        userFormDTO.setIp(ipUtil.getUserIp(request));

        Account account = accountService.insertAccount(userFormDTO.toEntity());

        model.addAttribute("join", true);
        model.addAttribute("email", account.getEmail());
        model.addAttribute("username", account.getUsername());
        model.addAttribute("createdDate", account.defaultCreateDate("yyyy년 MM월 dd일"));

        return "account/joinResult";
    }

    @GetMapping("/joinresult")
    public String joinResult(Model model) {
        /* 회원가입이 완료된 사용자만 접근 가능하도록 조건문 설정 */
        if (model.getAttribute("email") == null || model.getAttribute("username") == null
                || model.getAttribute("nickname") == null) {
            return "redirect:/";
        }

        return "account/joinResult";
    }

    /*
     * 아이디 찾기
     * */
    @GetMapping("/findbyemail")
    public String findByEmail(ModelMap model) {
        FindByEmailDTO findByEmailDTO = new FindByEmailDTO();
        model.addAttribute("findByEmailDTO", findByEmailDTO);

        return "account/findByEmail";
    }

    @PostMapping("/findbyemail")
    public String findByEmail(
            @Valid @ModelAttribute FindByEmailDTO findByEmailDTO,
            BindingResult bindingResult,
            ModelMap model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("findByEmailDTO", findByEmailDTO);

            return "account/findByEmail";
        }

        Optional<Account> account = accountService
                .findEmailByUsernameAndPhoneNumber(findByEmailDTO.getUsername(),
                        findByEmailDTO.getPhoneNumber());

        if (account.isPresent()) {
            model.addAttribute("findByEmail", true);
            model.addAttribute("email", account.get().getEmail());
            model.addAttribute("username", account.get().getUsername());
            model.addAttribute("createdDate", account.get().defaultCreateDate("yyyy년 MM월 dd일"));

            return "account/joinResult";
        }

        model.addAttribute("notFound", "찾을수 없습니다.");

        return "account/findByEmail";
    }

}
