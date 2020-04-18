package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.SHOP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjunpro.shop.dto.ProductOrderDTO;
import com.jjunpro.shop.dto.ProductSetDTO;
import com.jjunpro.shop.dto.ReceiptDTO;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.FileStorageServiceImpl;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import com.jjunpro.shop.util.StringBuilderUtil;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private final FileStorageServiceImpl  fileStorageService;
    private final StringBuilderUtil       stringBuilderUtil;

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
        String[] productArr = this.stringBuilderUtil
                .classifyUnData(productOrderDTO.getProductIds());
        String[] quantityArr = this.stringBuilderUtil
                .classifyUnData(productOrderDTO.getProductQuantitys());
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

        /* 유저의 정보를 기준으로 Form Init */
        productOrderDTO.setOrderName(userDetails.getAccount().getUsername());
        productOrderDTO.setOrderEmail(userDetails.getAccount().getEmail());
        productOrderDTO.setOrderPhone(userDetails.getAccount().getPhoneNumber());
        productOrderDTO.setPostcode(userDetails.getAccount().getPostcode());
        productOrderDTO.setAddr1(userDetails.getAccount().getAddr1());
        productOrderDTO.setAddr2(userDetails.getAccount().getAddr2());

        /* 포인트 사용금지가 하나라도 있으면 포인트 사용 금지 */
        productOrderDTO.setPointEnabled(true);

        for(Product product : productList) {
            if(!product.getPointEnabled()) {
                productOrderDTO.setPointEnabled(false);
                break;
            }
        }

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

    /* 구매 후 영수증 */

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
            model.addAttribute("productOrder", dbProductOrder.get());
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

    @GetMapping("/receipt/list")
    public String receiptList(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        AccountContext userDetails = (AccountContext) this.accountService
                .loadUserByUsername(principal.getUsername());

        List<ProductOrder> dbProductOrderList = this.productOrderService
                .findByAccountIdList(userDetails.getAccount().getId());

        model.addAttribute("productOrderList", dbProductOrderList);

        return SHOP.concat("/productReceiptList");
    }

    /* 장바구니 */

    @GetMapping("/cart")
    public String cart(
            Model model,
            HttpServletRequest request
    ) throws JsonProcessingException {
        ObjectMapper  objectMapper = new ObjectMapper();
        Cookie[]      myCookies    = request.getCookies();
        ProductSetDTO productSet   = null;

        /* 클라이언트에 저장된 Cookie 값을 확인하여 장바구니 정보를 가져옵니다. */
        for (Cookie myCookie : myCookies) {
            if (myCookie.getName().equals("Cart") && !myCookie.getValue().isEmpty()) {
                String cookie = URLDecoder.decode(myCookie.getValue(), StandardCharsets.UTF_8);
                productSet = objectMapper.readValue(cookie, ProductSetDTO.class);
            }
        }

        List<Product> productList = new ArrayList<>();

        if (productSet != null) {
            /* 클라이언트에서 전달받은 주문하려는 상품 목록과 수량으로 상품 DB 탐색 */
            String[] productArr = this.stringBuilderUtil
                    .classifyUnData(productSet.getSetId());
            String[] quantityArr = this.stringBuilderUtil
                    .classifyUnData(productSet.getSetQuantity());

            this.getProduct(productArr, quantityArr, productList);

        }

        model.addAttribute("productList", productList);

        return SHOP.concat("/productCart");
    }

    @PostMapping("/cart")
    public String cartSet(
            @Valid Product product,
            @ModelAttribute ProductSetDTO productSet,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);

            return SHOP.concat("/productView");
        }

        return "redirect:/order/cart";
    }

    /* 구매하려는 상품의 목록 & 수량을 DB 에서 가져옵니다. */
    private void getProduct(String[] idArr, String[] quantityArr, List<Product> productList) {
        Map<Long, Integer> productMap = new HashMap<>();

        int i = 0;
        for (String id : idArr) {
            productMap.put(Long.parseLong(id), Integer.parseInt(quantityArr[i]));
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
