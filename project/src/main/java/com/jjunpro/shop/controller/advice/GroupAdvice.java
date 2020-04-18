package com.jjunpro.shop.controller.advice;

import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * ShopGroup 등록된 카테고리 리스트를 담아서 모든 Controller 위치에 전송합니다.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GroupAdvice {

    private final ShopGroupServiceImpl shopGroupService;

    @ModelAttribute
    public void addAttributes(Model model) {
        /* 분류 리스트를 불러옵니다. */
        List<ShopGroup> shopGroupList = this.shopGroupService.findByIsNullParentShopGroupId(false);
        model.addAttribute("shopGroupList", shopGroupList);
    }
}