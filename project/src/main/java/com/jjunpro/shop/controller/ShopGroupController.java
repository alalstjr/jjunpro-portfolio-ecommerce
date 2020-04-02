package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINGROUP;

import com.jjunpro.shop.dto.ShopGroupDTO;
import com.jjunpro.shop.model.ShopGroup;
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

/*
 * TODO 관리자 권한으로만 접근하도록 설정해야 합니다.
 * */

@Controller
@RequiredArgsConstructor
@RequestMapping("/shopgroup")
public class ShopGroupController {

    private final ShopGroupServiceImpl shopGroupService;
    private final IpUtil               ipUtil;

    @GetMapping("")
    public String index(
            Model model
    ) {
        List<ShopGroup> shopGroupList = shopGroupService.getAll();
        model.addAttribute("shopGroupList", shopGroupList);

        return ADMINGROUP.concat("/index");
    }

    @GetMapping("/set")
    public String initSet(
            Model model,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long parentShopGroupId
    ) {
        ShopGroupDTO shopGroupDTO = new ShopGroupDTO();

        /* 수정 id */
        if(id != null) {
            ShopGroup shopGroup = shopGroupService.findById(id);
            shopGroupDTO.setId(id);
            shopGroupDTO.setEnabled(shopGroup.getEnabled());
            shopGroupDTO.setShopName(shopGroup.getShopName());
            shopGroupDTO.setPriority(shopGroup.getPriority());
            shopGroupDTO.setParentShopGroupId(shopGroup.getParentShopGroupId());
        }

        /* 부모노드 id */
        if(parentShopGroupId != null) {
            shopGroupDTO.setParentShopGroupId(parentShopGroupId);
        }

        model.addAttribute("shopGroupDTO", shopGroupDTO);

        return ADMINGROUP.concat("/setGroupForm");
    }

    @PostMapping("/set")
    public String processSet(
            HttpServletRequest request,
            @Valid ShopGroupDTO shopGroupDTO,
            BindingResult bindingResult
    ) {
        shopGroupDTO.setIp(ipUtil.getUserIp(request));
        shopGroupService.set(shopGroupDTO.toEntity());

        return "redirect:/shopgroup";
    }

    @PostMapping("/delete")
    public void delete(Long id) {
        shopGroupService.delete(id);
    }
}
