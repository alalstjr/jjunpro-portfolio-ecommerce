package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jjunpro.shop.dto.ShopGroupDTO;
import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ShopGroupServiceImpl shopGroupService;

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /* 분류가 등록되지 않은상태에서 상품등록 접근시 message FlashMap 전송과 링크이동이 되는지 확인합니다. */
    @Test
    public void initSet() throws Exception {
        mockMvc.perform(get("/product/set"))
                .andExpect(flash().attributeExists("message"))
                .andDo(print());
    }

    /* 상품등록이 정상적으로 등록되는지 확인합니다. */
    @Test
    public void set() throws Exception {

        /* 임시 분류를 하나 생성합니다. */
        ShopGroup shopGroup = getShopGroup();

        mockMvc.perform(post("/product/set")
                .param("enabled", "true")
                .param("productName", "닌텐도 DS")
                .param("price", "10000")
                .param("shopGroupIds", shopGroup.getId().toString() + ",")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }

    @Test
    public void delete() {
    }

    /* File Upload 정상등록이 되는지 확인합니다. */
    @Test
    public void fileUpload() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "hello file".getBytes()
        );

        mockMvc.perform(multipart("/file/set")
                .file(multipartFile)
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    private ShopGroup getShopGroup() {
        ShopGroupDTO shopGroupDTO = ShopGroupDTO.builder()
                .ip("0.0.0.0")
                .enabled(true)
                .shopName("전자제품")
                .build();

        ShopGroup shopGroup = shopGroupDTO.toEntity();
        shopGroupService.set(shopGroup);

        return shopGroup;
    }
}