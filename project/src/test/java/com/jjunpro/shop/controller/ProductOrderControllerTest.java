package com.jjunpro.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jjunpro.shop.dto.ProductAccessDTO;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.service.ProductAccessServiceImpl;
import com.jjunpro.shop.service.SecurityServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    ProductAccessServiceImpl productAccessService;

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

    @Test
    public void test() {
        ProductAccessDTO ageAccessByShopGroup = productAccessService.getAgeAccessByProductId();
        System.out.println(ageAccessByShopGroup.getAgeTwenty());
    }

    @Test
    public void ageConverter() {
        // value  => 1, 3, 1, 1, 2, 1, 2, 5, 4, 1, 2, 5, 4
        // result => { 1 = 2 }, { 2 = 6 }, { 3 = 1 }
        List<Long> values = new ArrayList<>();
        values.add(1L);
        values.add(1L);
        values.add(2L);
        values.add(2L);
        values.add(2L);
        values.add(3L);
        values.add(3L);
        values.add(3L);
        values.add(3L);

        long l = System.currentTimeMillis();

        Long                   temp      = values.get(0);
        int                    tempCount = 1;
        HashMap<Long, Integer> hashMap   = new HashMap<>();

        int i = 0;
        for (Long value : values) {
            if (!temp.equals(value)) {
                hashMap.put(temp, tempCount - 1);
                temp = value;
                tempCount = 1;
            }

//            System.out.println("val:" + value + " tempCount:" + tempCount + " i: " + i + " values.size():" + values.size());

            if(i == values.size() - 1) {
                hashMap.put(value, tempCount);
                tempCount = 1;
                continue;
            }

            tempCount++;
            i++;
        }

        List<Long> keySetList = new ArrayList<>(hashMap.keySet());
        keySetList.sort((o1, o2) -> (hashMap.get(o2).compareTo(hashMap.get(o1))));

        for (Long key : keySetList) {
            System.out.println("key : " + key + " / " + "value : " + hashMap.get(key));
        }

        System.out.println("Time : " + (System.currentTimeMillis() - l));
    }

    /**
     * 분류 : 핸드폰(id=1), 노트북(id=2), 아이패드(id=3), 이어폰(id=4), 마우스(id=5)
     *
     * 사용자가 노트북(id=2) 분류에 들어가면 DB 에 사용자 정보와 분류id 값이 저장됨 ex) 연령대 10대 인 경우 10대가 저장
     * Table Name ProductAccess
     * Colum id  : 노트북(id=2)
     * Colum age : 10
     *
     * 만약에 유입량이 많아서 수많은 사용자가 분류에 왔다갔다 하므로써 1000000 (백만) 명이 노트북(id=2) 접근했다.
     * 하지만 노트북만 유입하지않고 핸드폰, 아이패드... 등등 1000000 (백만) 이상이 분류를 확인했다.
     *
     * 10대 사용자가 각각의 분류를 본 횟수는
     * 핸드폰(id=1) = 1000000(백만),
     * 노트북(id=2) = 500000(오십만),
     * 아이패드(id=3) = 1000000(천만),
     * 이어폰(id=4) = 10000(만명),
     * 마우스(id=5) = 100(백명)
     *
     * 여기서 원하는것은 김민석은 10대가 가장 많이 본 분류를 3개 를 보고싶다.
     *
     * 완벽한 알고리즘으로 3개 핸드폰, 아이패드, 노트북 출력하고싶다.
     *
     * 근데 민석이의 알고리즘은?
     * 우선 DB 에서 10대가 본 분류를 '모두' 불러온다.
     *
     * 다음 java 코드에서 정렬 후 큰 순서 원하는 정보를 3개만 꺼내서 확인한다.
     *
     * 여기서 문제점은 '모두' 불러와 sort 해서 3개만 불러오는 작업이 수많은 연산 작업이 소모된다.
     * 시간이 지날수록 분류를 보는 사용자의 횟수도 커지고 연산속도도 점차 느려질것이다.
     *
     * 이것이 맞는걸까?
     */
}