package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.SHOP;

import com.jjunpro.shop.dto.ProductOrderDTO;
import com.jjunpro.shop.dto.ProductSetDTO;
import com.jjunpro.shop.dto.ReceiptDTO;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@SessionAttributes({"productList", "productSet"})
public class ProductOrderController {

    private final IpUtil                  ipUtil;
    private final ProductServiceImpl      productService;
    private final ProductOrderServiceImpl productOrderService;

    /* Test Code */
    @PostMapping("/set")
    public void set(
            ProductOrderDTO productOrderDTO,
            BindingResult bindingResult,
            Model model
    ) {
        this.productOrderService.set(productOrderDTO.toEntity());
    }

    @GetMapping("/view")
    public String view(
            @RequestParam Long id,
            Model model
    ) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
        }

        model.addAttribute("productSet", new ProductSetDTO());

        return SHOP.concat("/productView");
    }

    @PostMapping("/view")
    public String viewSet(
            @Valid @ModelAttribute Product product,
            @Valid @ModelAttribute ProductSetDTO productSet,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("ProductSetDTO", productSet);

            return SHOP.concat("/productView");
        }

        /* Session 저장소에 상품 id, 수량 등등 기타정보를 담아서 주문서로 넘깁니다. */
        model.addAttribute("productSet", productSet);

        return "redirect:/order/form";
    }

    @GetMapping("/form")
    public String order(
            Model model
    ) {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO();
        ProductSetDTO   productSet      = (ProductSetDTO) model.getAttribute("productSet");
        assert productSet != null;
        String[]      productArr  = productSet.getId().split(",");
        String[]      quantityArr = productSet.getQuantity().split(",");
        List<Product> productList = new ArrayList<>();

        this.getProduct(productArr, quantityArr, productList);

        model.addAttribute("productOrderDTO", productOrderDTO);
        model.addAttribute("productList", productList);

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
            String[]      productArr  = dbProductOrder.get().getProductIds().split(",");
            String[]      quantityArr = dbProductOrder.get().getProductQuantitys().split(",");
            List<Product> productList = new ArrayList<>();

            this.getProduct(productArr, quantityArr, productList);

            model.addAttribute("productOrder", dbProductOrder.get());
            model.addAttribute("productList", productList);
            model.addAttribute("message", message);
        }

        return SHOP.concat("/productReceipt");
    }

    @PostMapping("/receipt")
    public String receiptSet(
            @Valid ReceiptDTO id,
            Model model,
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

    /* 구매한 상품의 목록 & 수량을 가져옵니다. */
    private void getProduct(String[] productArr, String[] quantityArr, List<Product> productList) {
        int i = 0;
        for (String product : productArr) {
            /* 주문상품을 조회합니다. */
            Optional<Product> dbProduct = this.productService.findById(Long.parseLong(product));

            if (dbProduct.isPresent()) {
                /* 주문수량을 개별상품에 담습니다. */
                dbProduct.get().setOrderQuantity(quantityArr[i]);
                productList.add(dbProduct.get());
            }

            i++;
        }
    }
}
