package com.jjunpro.shop.controller;

import com.jjunpro.shop.dto.ShopGroupDTO;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shopgroup")
public class ShopGroupController {

    private final ShopGroupServiceImpl shopGroupService;
    private final IpUtil               ipUtil;

    @PostMapping("/create")
    public void createGroup(ShopGroupDTO shopGroupDTO, HttpServletRequest request) {
        shopGroupDTO.setIp(ipUtil.getUserIp(request));
        shopGroupService.insertShopGroup(shopGroupDTO.toEntity());
    }

    @PostMapping("/delete")
    public void deleteGroup(Long id) {
        shopGroupService.deleteShopGroup(id);
    }
}
