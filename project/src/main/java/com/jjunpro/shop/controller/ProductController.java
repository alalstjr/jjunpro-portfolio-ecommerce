package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINGROUP;
import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;

import com.jjunpro.shop.dto.ProductDTO;
import com.jjunpro.shop.dto.ShopGroupDTO;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.service.ProductService;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl   productService;
    private final ShopGroupServiceImpl shopGroupService;
    private final IpUtil               ipUtil;

    @GetMapping("")
    public String index(
            Model model
    ) {
        List<Product> productList = productService.findAll();
        model.addAttribute("productList", productList);

        return ADMINPRODUCT.concat("/index");
    }

    @GetMapping("/set")
    public String initSet(
            Model model,
            @RequestParam(required = false) Long id,
            RedirectAttributes redirectAttributes
    ) {
        /* 분류가 하나이상 존재하는지 확인합니다. */
        if(shopGroupService.allCount() == 0) {
            redirectAttributes.addFlashAttribute("message", "분류가 하나이상 존재해야 상품등록이 가능합니다.");

            return "redirect:/shopgroup/set";
        }

        ProductDTO productDTO = new ProductDTO();

        /* 수정 id */
        if (id != null) {
            Product product = productService.findById(id);
            model.addAttribute("productDTO", product);
        } else {
            model.addAttribute("productDTO", productDTO);
        }

        this.getShopGroupList(model);

        return ADMINPRODUCT.concat("/setProductForm");
    }

    @PostMapping("/set")
    public String set(
            HttpServletRequest request,
            @Valid ProductDTO productDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            this.getShopGroupList(model);
            model.addAttribute("productDTO", productDTO);

            return ADMINPRODUCT.concat("/setProductForm");
        }

        productDTO.setIp(ipUtil.getUserIp(request));
        productService.set(productDTO.toEntity());

        return "redirect:/product";
    }

    /* RedirectAttributes 사용하여 그룹 index 페이지에 상태 메세지 Attributes 전달합니다.  */
    @PostMapping("/delete")
    public String delete(Long id, RedirectAttributes model) {
        model.addFlashAttribute("message", productService.delete(id));

        return "redirect:/product";
    }

    /* 분류 리스트를 불러옵니다. */
    private void getShopGroupList(Model model) {
        List<ShopGroup> shopGroupList = shopGroupService.findByIsNullParentShopGroupId();
        model.addAttribute("shopGroupList", shopGroupList);
    }
}
