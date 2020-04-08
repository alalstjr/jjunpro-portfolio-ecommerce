package com.jjunpro.shop.controller;

import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.service.ProductServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProductServiceImpl productService;

    @GetMapping("/")
    public String main(Model model) {
        List<Product> productList = productService.findAll(false);
        model.addAttribute("productList", productList);

        return "main/main";
    }
}
