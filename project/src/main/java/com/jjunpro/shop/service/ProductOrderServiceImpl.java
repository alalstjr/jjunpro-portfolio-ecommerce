package com.jjunpro.shop.service;

import com.jjunpro.shop.exception.DataNullException;
import com.jjunpro.shop.exception.ProductOrderException;
import com.jjunpro.shop.mapper.AccountMapper;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.mapper.ProductOrderMapper;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ProductOrderServiceImpl.class);

    private final ProductMapper      productMapper;
    private final AccountMapper      accountMapper;
    private final ProductOrderMapper productOrderMapper;

    private Integer totalAmount       = 0;
    private Boolean totalPointEnabled = false;

    @Override
    public ProductOrder set(ProductOrder productOrder) {
        /* 로그인한 유저의 정보를 가져옵니다. */
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        UserDetails       userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Account> account     = accountMapper.findByEmail(userDetails.getUsername());

        /* 상품의 id, 갯수를 가져와 계산합니다. */
        Map<Long, Integer> productList = productOrder.getProductList();
        Iterator<Long>     iterator    = productList.keySet().iterator();

        /* 상품의 기본가격 저장변수 */
        StringBuilder originPrice = new StringBuilder();

        /* 구매하는 상품 수 만큼 반복처리 */
        while (iterator.hasNext()) {
            Long    id       = iterator.next();
            Integer quantity = productList.get(id);

            Optional<Product> dbProduct = this.productMapper.findById(id);
            if (dbProduct.isPresent()) {
                if (dbProduct.get().getQuantity() > 0) {
                    Integer price        = dbProduct.get().getPrice();
                    Short   discount     = dbProduct.get().getDiscount();
                    Boolean pointEnabled = dbProduct.get().getPointEnabled();

                    /* 상품의 기본가격을 {,} 구분하여 저장합니다. */
                    originPrice.append(price).append(",");

                    /* 해당 상품의 할인률 계산 */
                    if (discount != null) {
                        this.totalAmount += this.percentage(price, discount) * quantity;
                    } else {
                        this.totalAmount += price * quantity;
                    }

                    /* 상품의 포인트 사용이 하나라도 허용되있는 경우 true */
                    if (pointEnabled) {
                        this.totalPointEnabled = true;
                    }

                    /* 구매한 상품 갯수만큼 기존상품 갯수 차감 */
                    Integer afterQuantity = dbProduct.get().getQuantity() - quantity;
                    productMapper.updateQuantity(dbProduct.get().getId(), afterQuantity);
                } else {
                    throw new ProductOrderException("상품의 수량이 존재하지 않습니다.");
                }
            } else {
                throw new DataNullException("상품이 존재하지 않습니다.");
            }
        }

        /* 유저의 적립금 사용 계산 */
        if (this.totalPointEnabled && productOrder.getPoint() != null) {
            if (account.isPresent()) {
                Integer point = account.get().getPoint();

                /* 사용하려는 적립금과 유저의 적립금 차이가 있는지 확인합니다. */
                if (point != null && point >= productOrder.getPoint()) {
                    this.totalAmount = this.totalAmount - productOrder.getPoint();
                    /* 사용자의 포인트 감소 */
                    int afterPoint = point - productOrder.getPoint();
                    accountMapper.updatePoint(account.get().getId(), afterPoint);
                }
            }
        }

        /* 구매하는 유저의 정보를 저장합니다. */
        account.ifPresent(value -> productOrder.setAccountId(value.getId()));

        /*
         * 상품의 기본가격의 마지막 {,} 문자를 제거합니다.
         * 상품의 기본가격을 저장합니다.
         */
        productOrder.setProductAmounts(originPrice.substring(0, originPrice.length() - 1));

        /*
         * 주문서의 기본상태 설정
         * orderState 0 인경우 주문완료
         * orderState 1 인경우 주문접수(결제확인) 완료
         * orderState 2 인경우 배송완료 완료
         * orderState 3 인경우 주문취소 완료
         */
        productOrder.setEnabled(true);
        productOrder.setOrderState((short) 0);

        /* 최종 결제금액 설정 */
        productOrder.setTotalAmount(this.totalAmount);
        productOrderMapper.insert(productOrder);

        return productOrder;
    }

    @Override
    public Optional<ProductOrder> findById(Long id) {
        return productOrderMapper.findById(id);
    }

    @Override
    public String orderCancel(Long id) {
        Optional<ProductOrder> dbProductOrder = productOrderMapper.findById(id);
        if (dbProductOrder.isPresent()) {
            if (!dbProductOrder.get().getOrderState().equals((short) 3)) {
                this.productOrderMapper.orderCancel(id);

                return "주문이 취소되었습니다.";
            }
        }

        return "주문을 취소할 수 없습니다.";
    }

    /* 백분율 계산 메소드 */
    private Integer percentage(Integer price, Short percent) {
        return price - (price * percent / 100);
    }
}
