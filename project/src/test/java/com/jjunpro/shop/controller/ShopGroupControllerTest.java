package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ShopGroupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ShopGroupServiceImpl shopGroupService;

    /* 대분류 생성 */
    @Test
    @Transactional
    public void createGroup() throws Exception {
        mockMvc.perform(post("/shopgroup/set")
                .param("name", "test")
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /* 대분류 하위에 소분류 생성 */
    @Test
    @Transactional
    public void createGroupChildren() throws Exception {

        ShopGroup shopGroup = getShopGroup();

        mockMvc.perform(post("/shopgroup/set")
                .param("name", "test")
                .param("parentShopGroupId", shopGroup.getId().toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteGroup() throws Exception {
        ShopGroup shopGroup = getShopGroup();

        /*
         * 대분류 하위에 소분류 생성
         * 하위 분류가 존재하는 경우 삭제를 못하는지 확인하는 코드
         */
        ShopGroup shopGroupChildren = ShopGroup.builder()
                .ip("0.0.0.0")
                .shopName("test-1-1")
                .parentShopGroupId(shopGroup.getId())
                .build();

        shopGroupService.set(shopGroupChildren);

        mockMvc.perform(post("/shopgroup/delete")
                .param("id", shopGroup.getId().toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updateGroup() throws Exception {

        ShopGroup shopGroup = getShopGroup();

        mockMvc.perform(post("/shopgroup/set")
                .param("id",shopGroup.getId().toString())
                .param("name", "test-mode")
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private ShopGroup getShopGroup() {
        ShopGroupDTO shopGroupDTO = ShopGroupDTO.builder()
                .ip("0.0.0.0")
                .shopName("test-1")
                .build();

        ShopGroup shopGroup = shopGroupDTO.toEntity();
        shopGroupService.set(shopGroup);

        return shopGroup;
    }
}