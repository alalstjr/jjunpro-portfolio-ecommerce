package com.jjunpro.shop.controller;

import com.jjunpro.shop.dto.ProductDTO;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl ProductService;
    private final IpUtil             ipUtil;

    @PostMapping("/set")
    public void set(
            HttpServletRequest request,
            @Valid ProductDTO productDTO,
            BindingResult bindingResult
    ) {
        productDTO.setIp(ipUtil.getUserIp(request));
        ProductService.set(productDTO.toEntity());
    }

    @PostMapping("/delete")
    public void delete(Long id) {
        ProductService.delete(id);
    }
}
