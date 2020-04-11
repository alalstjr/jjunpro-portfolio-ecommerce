package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.SHOP;

import com.jjunpro.shop.dto.ProductOrderDTO;
import com.jjunpro.shop.dto.ProductSetDTO;
import com.jjunpro.shop.dto.ReceiptDTO;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@SessionAttributes({"product", "productList", "productOrderDTO", "productSet"})
public class ProductOrderController {

    private final IpUtil                  ipUtil;
    private final ProductServiceImpl      productService;
    private final ProductOrderServiceImpl productOrderService;
    private final AccountServiceImpl      accountService;

    /* Test Code */
    @PostMapping("/set")
    public void set(
            ProductOrderDTO productOrderDTO
    ) {
        this.productOrderService.set(productOrderDTO.toEntity());
    }

    @GetMapping("/view")
    public String view(
            @RequestParam Long id,
            Model model
    ) {
        Optional<Product> product = this.productService.findById(id);

        if (product.isPresent()) {
            model.addAttribute("product", product.get());
        }

        /* 클라이언트에서 전달받는 상품의 { id, 수량 } 정보를 저장하는 DTO */
        model.addAttribute("productSet", new ProductSetDTO());

        return SHOP.concat("/productView");
    }

    @PostMapping("/view")
    public String viewSet(
            @Valid Product product,
            @Valid @ModelAttribute ProductSetDTO productSet,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("productSet", productSet);

            return SHOP.concat("/productView");
        }

        /* Session 저장소에 상품 { id, 수량 } 정보를 담아서 주문서로 넘깁니다. */
        ProductOrderDTO productOrderDTO = new ProductOrderDTO();
        productOrderDTO.setProductIds(productSet.getSetId());
        productOrderDTO.setProductQuantitys(productSet.getSetQuantity());
        model.addAttribute("productOrderDTO", productOrderDTO);

        return "redirect:/order/form";
    }

    @GetMapping("/form")
    public String order(
            @ModelAttribute ProductOrderDTO productOrderDTO,
            Model model
    ) {
        /* 클라이언트에서 전달받은 주문하려는 상품 목록과 수량으로 상품 DB 탐색 */
        String[]      productArr  = productOrderDTO.getProductIds().split(",");
        String[]      quantityArr = productOrderDTO.getProductQuantitys().split(",");
        List<Product> productList = new ArrayList<>();

        this.getProduct(productArr, quantityArr, productList);

        /* 클라이언트가 보유한 포인트를 탐색 */
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        AccountContext userDetails = (AccountContext) this.accountService
                .loadUserByUsername(principal.getUsername());

        /* 클라이언트 사용자의 보유한 포인트를 저장 */
        productOrderDTO.setAccountPoint(userDetails.getAccount().getPoint());

        model.addAttribute("productList", productList);
        model.addAttribute("productOrderDTO", productOrderDTO);

        return SHOP.concat("/productOrder");
    }

    @PostMapping("/form")
    public String orderSet(
            @ModelAttribute List<Product> productList,
            @Valid @ModelAttribute ProductOrderDTO productOrderDTO,
            BindingResult bindingResult,
            Model model,
            SessionStatus sessionStatus,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productList", productList);
            model.addAttribute("productOrderDTO", productOrderDTO);

            return SHOP.concat("/productOrder");
        }

        productOrderDTO.setIp(ipUtil.getUserIp(request));

        ProductOrder productOrder = this.productOrderService.set(productOrderDTO.toEntity());

        /* 처리완료 후 DTO session 제거 */
        sessionStatus.isComplete();

        /* 영수증 확인을 위해서 구매 id 전송 */
        redirectAttributes.addAttribute("id", productOrder.getId());

        return "redirect:/order/receipt";
    }

    @GetMapping("/receipt")
    public String receipt(
            @Valid ReceiptDTO id,
            @RequestParam(required = false) String message,
            Model model,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/";
        }

        Optional<ProductOrder> dbProductOrder = this.productOrderService.findById(id.getId());

        if (dbProductOrder.isPresent()) {
            String[]      idArr       = dbProductOrder.get().getProductIds().split(",");
            String[]      quantityArr = dbProductOrder.get().getProductQuantitys().split(",");
            List<Product> productList = new ArrayList<>();

            this.getProduct(idArr, quantityArr, productList);

            model.addAttribute("productOrder", dbProductOrder.get());
            model.addAttribute("productList", productList);
            model.addAttribute("message", message);
        }

        return SHOP.concat("/productReceipt");
    }

    @PostMapping("/receipt")
    public String receiptSet(
            @Valid ReceiptDTO id,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addAttribute("id", id.getId());

        if (bindingResult.hasErrors()) {
            return "redirect:/order/receipt";
        }

        String orderCancel = this.productOrderService.orderCancel(id.getId());
        redirectAttributes.addAttribute("message", orderCancel);

        return "redirect:/order/receipt";
    }

    /* 구매하려는 상품의 목록 & 수량을 DB 에서 가져옵니다. */
    private void getProduct(String[] idArr, String[] quantityArr, List<Product> productList) {
        Map<Long, Integer> productMap = new HashMap<>();

        int i = 0;
        for (String id : idArr) {
            productMap.put(Long.parseLong(id.trim()), Integer.parseInt(quantityArr[i].trim()));
            i++;
        }

        for (Long id : productMap.keySet()) {
            Integer           quantity  = productMap.get(id);
            Optional<Product> dbProduct = this.productService.findById(id);

            if (dbProduct.isPresent()) {
                /* 주문수량을 개별상품에 담습니다. */
                dbProduct.get().setOrderQuantity(quantity);
                productList.add(dbProduct.get());
            }
        }
    }
}
