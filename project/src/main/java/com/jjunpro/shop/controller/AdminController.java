package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINACCOUNT;
import static com.jjunpro.shop.util.ClassPathUtil.ADMINORDER;

import com.jjunpro.shop.dto.ProductAccessAgeDTO;
import com.jjunpro.shop.dto.ProductAccessDTO;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.ProductAccessServiceImpl;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductOrderServiceImpl  productOrderService;
    private final AccountServiceImpl       accountService;
    private final ProductAccessServiceImpl productAccessService;

    @GetMapping("")
    public String index(Model model) {
        Integer dbTotalAmount     = this.productOrderService.findTotalAmountByOrderState();
        Integer dbTotalCount      = this.productOrderService.findCountByAll();
        Integer dbCountOrderState = this.productOrderService.findCountByOrderState();
        Integer dbCountAccount    = this.accountService.findCountByAll();
        /* 연령대별 분류 탐색유저 정보 */
        ProductAccessAgeDTO ageAccessByProduct = this.productAccessService
                .getAgeAccessByProduct();

        model.addAttribute("totalAmount", dbTotalAmount);
        model.addAttribute("totalCount", dbTotalCount);
        model.addAttribute("countOrderState", dbCountOrderState);
        model.addAttribute("countAccount", dbCountAccount);
        model.addAttribute("ageAccessByProduct", ageAccessByProduct);

        return "admin/index";
    }

    /* 주문관리 */

    @GetMapping("/order")
    public String order(Model model) {
        List<ProductOrder> dbProductOrderList = productOrderService.findAllAdmin();

        model.addAttribute("productOrderList", dbProductOrderList);

        return ADMINORDER.concat("/index");
    }

    @GetMapping("/order/form")
    public String orderForm(Model model, @RequestParam Long id) {
        Optional<ProductOrder> dbProductOrder = productOrderService.findById(id);

        if (dbProductOrder.isPresent()) {
            model.addAttribute("productOrder", dbProductOrder.get());
        }

        return ADMINORDER.concat("/setOrderForm");
    }

    @PostMapping("/order/form")
    public String orderSet(
            @RequestParam Long id,
            @RequestParam short orderState,
            RedirectAttributes redirectAttributes
    ) {
        this.productOrderService.updateOrderStateById(id, orderState);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addAttribute("message", "수정이 완료되었습니다.");

        return "redirect:/admin/order/form";
    }

    /* 유저관리 */

    @GetMapping("/account")
    public String account(Model model) {
        List<Account> dbAccountList = accountService.findAll();

        model.addAttribute("accountList", dbAccountList);

        return ADMINACCOUNT.concat("/index");
    }
}
