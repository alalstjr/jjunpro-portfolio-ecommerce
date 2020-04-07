package com.jjunpro.shop.controller;

import com.jjunpro.shop.dto.ProductOrderDTO;
import com.jjunpro.shop.service.ProductOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderServiceImpl productOrderService;

    @PostMapping("/set")
    public void set(
            ProductOrderDTO productOrderDTO,
            BindingResult bindingResult,
            Model model
    ) {
        this.productOrderService.set(productOrderDTO.toEntity());
    }
}
