package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jjunpro.shop.enums.UserRole;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
public class ProductOrderControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductOrderControllerTest.class);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    SecurityServiceImpl securityService;

    @Test
    public void set() throws Exception {
        /* 임시상품등록 */
        /*Product product = Product.builder()
                .ip("0.0.0.0")
                .shopGroupIds("1")
                .productName("상품 이름")
                .price(10000)
                .quantity(100)
                .buyMinQuantity(1)
                .buyMaxQuantity(10)
                .enabled(true)
                .pointEnabled(true)
                .discount((short) 10)
                .point((short) 10)
                .build();

        productMapper.insert(product);
        List<Product> all = productMapper.findAll();
        logger.info("data => ", all);
*/
        mockMvc.perform(post("/order/set")
                .param("ip", "0.0.0.0")
                .param("enabled", "true")
                .param("orderName", "김민석")
                .param("orderEmail", "alalstjr@naver.com")
                .param("orderPhone", "010-4021-1220")
                .param("postcode", "00000")
                .param("addr1", "서울특별시 중랑구")
                .param("addr2", "망우본동 감나무집 1층")
                .param("memo", "부재중시 문앞에 두세요.")
                .param("payment", "0")
                .param("point", "100")
                .param("productIds", "1")
                .param("productQuantitys", "10")
                .with(csrf())
                .with(user("alalstjr@naver.com").roles("USER")))
                .andExpect(status().isOk())
                .andDo(print());
    }
}