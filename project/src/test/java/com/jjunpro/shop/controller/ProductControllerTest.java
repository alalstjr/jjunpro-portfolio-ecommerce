package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jjunpro.shop.dto.ShopGroupDTO;
import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ShopGroupServiceImpl shopGroupService;

    @Test
    public void set() throws Exception {

        /* 임시 분류를 하나 생성합니다. */
        ShopGroup shopGroup = getShopGroup();

        mockMvc.perform(post("/product/set")
                .param("productName", "닌텐도 DS")
                .param("price", "10000")
                .param("shopGroupIds", shopGroup.getId().toString() + ",")
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    public void delete() {
    }

    private ShopGroup getShopGroup() {
        ShopGroupDTO shopGroupDTO = ShopGroupDTO.builder()
                .ip("0.0.0.0")
                .shopName("전자제품")
                .build();

        ShopGroup shopGroup = shopGroupDTO.toEntity();
        shopGroupService.set(shopGroup);

        return shopGroup;
    }
}