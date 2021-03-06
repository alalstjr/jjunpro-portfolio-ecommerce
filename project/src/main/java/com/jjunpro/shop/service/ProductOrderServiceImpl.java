package com.jjunpro.shop.service;

import com.jjunpro.shop.aspect.ProductOrderSet;
import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.exception.DataNullException;
import com.jjunpro.shop.exception.ProductOrderException;
import com.jjunpro.shop.mapper.AccountMapper;
import com.jjunpro.shop.mapper.FileStorageMapper;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.mapper.ProductOrderMapper;
import com.jjunpro.shop.model.Account;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductOrder;
import com.jjunpro.shop.util.FileUtil;
import com.jjunpro.shop.util.StringBuilderUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductMapper      productMapper;
    private final AccountMapper      accountMapper;
    private final FileStorageMapper  fileStorageMapper;
    private final ProductOrderMapper productOrderMapper;
    private final StringBuilderUtil  stringBuilderUtil;
    private final FileUtil           fileUtil;

    @ProductOrderSet
    @Override
    public ProductOrder set(ProductOrder productOrder) {
        int     totalAmount       = 0;
        int     totalReceivePoint = 0;
        boolean totalPointEnabled = false;

        Optional<Account> account = getAccount();

        /* 상품의 id, 갯수를 가져와 계산합니다. */
        Map<Long, Integer> orderInfo = productOrder.getOrderInfo();
        Iterator<Long>     iterator  = orderInfo.keySet().iterator();

        /*
         * 상품의 기본가격, 구매하는 상품이름, 썸네일 저장변수
         *
         * ex) { 1000, 2000, 3000 } or { "마우스", "키보드", "모니터" }
         */
        StringBuilder originPrice   = new StringBuilder();
        StringBuilder productNames  = new StringBuilder();
        StringBuilder productThumbs = new StringBuilder();

        /* 구매하는 상품 수 만큼 반복처리 */
        while (iterator.hasNext()) {
            Long    id       = iterator.next();
            Integer quantity = orderInfo.get(id);

            Optional<Product> dbProduct = this.productMapper.findById(id);
            if (dbProduct.isPresent()) {
                /* 상품의 수량체크와 구매가능 상태를 체크합니다. */
                if (dbProduct.get().getQuantity() > 0 &&
                        dbProduct.get().getEnabled() &&
                        dbProduct.get().getBuyMinQuantity() <= quantity &&
                        dbProduct.get().getBuyMaxQuantity() >= quantity) {
                    Integer price        = dbProduct.get().getPrice();
                    Short   discount     = dbProduct.get().getDiscount();
                    Boolean pointEnabled = dbProduct.get().getPointEnabled();
                    Short   point        = dbProduct.get().getPoint();

                    /* 구매하는 상품의 이름을 정리하여 주문서 제목을 생성합니다. */
                    productNames
                            .append(dbProduct.get().getProductName())
                            .append(',');

                    /* 상품의 썸네일을 지정합니다. */
                    if (dbProduct.get().getFileStorageIds() != null) {
                        productThumbs
                                .append(fileUtil.thumbnailCreate(
                                        dbProduct.get().getFileStorageIds(),
                                        DomainType.PRODUCT,
                                        DomainType.PRODUCTORDER
                                ))
                                .append(',');
                    }

                    /* 적립금 계산 */
                    totalReceivePoint += (price - (price * discount / 100)) * point / 100;

                    /* 해당 상품의 할인률 계산 */
                    if (discount > 0) {
                        totalAmount += (price - (price * discount / 100)) * quantity;

                        /* 상품의 기본가격을 {,} 구분하여 저장합니다. (할인이 적용된 기본가격) */
                        originPrice.append(price - (price * discount / 100)).append(',');
                    } else {
                        totalAmount += price * quantity;

                        /* 상품의 기본가격을 {,} 구분하여 저장합니다. */
                        originPrice.append(price).append(',');
                    }

                    /* 상품의 포인트 사용이 하나라도 허용되있는 경우 true */
                    if (pointEnabled) {
                        totalPointEnabled = true;
                    }

                    /* 구매한 상품 갯수만큼 기존상품 갯수 차감 */
                    Integer afterQuantity = dbProduct.get().getQuantity() - quantity;
                    this.productMapper.updateQuantity(dbProduct.get().getId(), afterQuantity);
                } else {
                    throw new ProductOrderException("상품의 수량이 존재하지 않거나 구매할수 없는 상품입니다.");
                }
            } else {
                throw new DataNullException("상품이 존재하지 않습니다.");
            }
        }

        /* 상품의 최종 적립금을 설정합니다. */
        productOrder.setReceivePoint(totalReceivePoint);

        /*
         * 유저의 적립금 사용 or 적립금 추가 계산
         * 적립금사용이 허용되있고 & 사용하는 포인트가 존재하며 & 구매하려는 상품의 가격이 10000원 이상인지 확인합니다.
         */
        if (account.isPresent()) {
            Integer point = account.get().getPoint();

            if (totalPointEnabled && productOrder.getUsePoint() != null
                    && productOrder.getUsePoint() > 0
                    && totalAmount >= 10000) {
                int afterPoint;

                /* 사용하려는 적립금과 유저의 적립금 차이가 있는지 확인합니다. */
                if (point != null && point >= productOrder.getUsePoint()) {
                    /* 상품보다 사용하는 포인트가 더 크거나 같은 경우 구매하는 가격만큼 포인트 차감 */
                    /* 사용자의 적립금 감소 */
                    if (totalAmount <= productOrder.getUsePoint()) {
                        productOrder.setUsePoint(totalAmount);
                        afterPoint = point - totalAmount;
                        totalAmount = 0;
                    } else {
                        afterPoint = point - productOrder.getUsePoint();
                        totalAmount = totalAmount - productOrder.getUsePoint();
                    }

                    this.accountMapper.updatePoint(account.get().getId(), afterPoint);

                    productOrder.setReceivePoint(0);
                }
            } else {
                /* 적립금을 사용하지 않는경우 0 으로 초기화 null 방지 */
                productOrder.setUsePoint(0);

                /* 사용자의 적립금 증가 */
                int afterPoint = point + productOrder.getReceivePoint();
                this.accountMapper.updatePoint(account.get().getId(), afterPoint);
            }
        }

        /* 상품의 썸네일을 주문서에 저장합니다. */

        /* 구매하는 상품의 종류와 갯수를 저장합니다. */
        productOrder.idsAndQuantitysSet(orderInfo);

        /* 구매하는 유저의 정보를 저장합니다. */
        account.ifPresent(value -> productOrder.setAccountId(value.getId()));

        /*
         * 상품의 { 기본가격, 상품이름, 상품썸네일 } 을 저장합니다.
         */
        productOrder.setProductAmounts(originPrice.toString());
        productOrder.setProductNames(productNames.toString());
        productOrder.setProductThumbs(productThumbs.toString());

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
        productOrder.setTotalAmount(totalAmount);
        this.productOrderMapper.insert(productOrder);

        return productOrder;
    }

    @Override
    public Optional<ProductOrder> findById(Long id) {
        Optional<ProductOrder> dbProductOrder = this.productOrderMapper.findById(id);

        /* 주문한 상품의 정보 ProductOrder -> Product 객체에 저장 */
        if (dbProductOrder.isPresent()) {
            List<Product> productList = new ArrayList<>();
            String[] nameArr = this.stringBuilderUtil
                    .classifyUnData(dbProductOrder.get().getProductNames());
            String[] quantityArr = this.stringBuilderUtil
                    .classifyUnData(dbProductOrder.get().getProductQuantitys());
            String[] amountArr = this.stringBuilderUtil
                    .classifyUnData(dbProductOrder.get().getProductAmounts());
            String[] thumbArr = this.stringBuilderUtil
                    .classifyUnData(dbProductOrder.get().getProductThumbs());

            int i = 0;
            for (String name : nameArr) {
                Integer productQuantity = Integer.parseInt(quantityArr[i]);
                Integer productAmount   = Integer.parseInt(amountArr[i]);

                Product product = Product
                        .builder()
                        .productName(name)
                        .quantity(productQuantity)
                        .price(productAmount)
                        .build();

                /* 썸네일 저장은 필수항목이 아니므로 존재 체크 후 저장 */
                if (!thumbArr[i].isEmpty()) {
                    Long productThumb = Long.parseLong(thumbArr[i]);
                    Optional<FileStorage> dbFileStorage = this.fileStorageMapper
                            .findById(productThumb);

                    dbFileStorage.ifPresent(
                            fileStorage -> product.setThumbnail(fileStorage.getFileDownloadUri())
                    );
                }

                productList.add(product);

                i++;
            }

            dbProductOrder.get().setProductList(productList);
        }

        return dbProductOrder;
    }

    @Override
    public List<ProductOrder> findByAccountIdList(Long accountId) {
        List<ProductOrder> dbProductOrder = this.productOrderMapper.findByAccountIdList(accountId);

        /* Product 각각의 썸네일 이미지를 불러옵니다. */
        for (ProductOrder productOrder : dbProductOrder) {
            String[] thumbArr = this.stringBuilderUtil
                    .classifyUnData(productOrder.getProductThumbs());

            /* 썸네일 저장은 필수항목이 아니므로 존재 체크 후 저장 */
            if (!thumbArr[0].isEmpty()) {
                Optional<FileStorage> dbFileStorage = this.fileStorageMapper
                        .findById(Long.parseLong(thumbArr[0]));

                dbFileStorage.ifPresent(
                        fileStorage -> productOrder
                                .setProductThumb(fileStorage.getFileDownloadUri())
                );
            }
        }

        return this.productOrderMapper.findByAccountIdList(accountId);
    }

    @Override
    public String orderCancel(Long id, Boolean ignore) {
        Optional<ProductOrder> dbProductOrder;

        if (ignore) {
            dbProductOrder = this.productOrderMapper.findByIdAdmin(id);
        } else {
            dbProductOrder = this.productOrderMapper.findById(id);
        }

        if (dbProductOrder.isPresent()) {
            if (!dbProductOrder.get().getOrderState().equals((short) 3)) {
                Optional<Account> account = getAccount();

                /* 구매한 상품의 수량, 포인트 회수 */
                Map<Long, Integer> productMap = new HashMap<>();
                String[] idArr = this.stringBuilderUtil
                        .classifyUnData(dbProductOrder.get().getProductIds());
                String[] quantityArr = this.stringBuilderUtil
                        .classifyUnData(dbProductOrder.get().getProductQuantitys());

                /* 상품 수량 반환 */
                int i = 0;
                for (String productId : idArr) {
                    productMap.put(Long.parseLong(productId), Integer.parseInt(quantityArr[i]));
                    i++;
                }

                for (Long productId : productMap.keySet()) {
                    Integer           quantity  = productMap.get(productId);
                    Optional<Product> dbProduct = this.productMapper.findById(productId);

                    if (dbProduct.isPresent()) {
                        /* DB 존재하는 상품의 수량 + 취소하는 상품의 수량 */
                        int afterQuantity = dbProduct.get().getQuantity() + quantity;
                        dbProduct.get().setQuantity(afterQuantity);
                        this.productMapper.update(dbProduct.get());
                    }
                }

                /* 사용한 포인트 반환 */
                if (account.isPresent()) {
                    Integer point      = account.get().getPoint();
                    int     afterPoint = 0;

                    if (dbProductOrder.get().getUsePoint() == 0) {
                        afterPoint = point - dbProductOrder.get().getReceivePoint();
                    } else {
                        afterPoint = point + dbProductOrder.get().getUsePoint();
                    }

                    this.accountMapper.updatePoint(account.get().getId(), afterPoint);
                }

                /* 주문서의 상태변경 */
                /* 주문의 상태를 취소로 변경합니다. */
                this.productOrderMapper.orderCancel(id);

                return "주문이 취소되었습니다.";
            }
        }

        return "주문을 취소할 수 없습니다.";
    }

    @Override
    public Integer findTotalAmountByOrderState() {
        return productOrderMapper.findTotalAmountByOrderState();
    }

    @Override
    public Integer findCountByAll() {
        return this.productOrderMapper.findCountByAll();
    }

    @Override
    public Integer findCountByOrderState() {
        return this.productOrderMapper.findCountByOrderState();
    }

    @Override
    public List<ProductOrder> findAllAdmin() {
        return this.productOrderMapper.findAllAdmin();
    }

    @Override
    public void updateOrderStateById(Long id, short orderState) {
        this.productOrderMapper.updateOrderStateById(id, orderState);
    }

    /* 로그인한 유저의 정보를 가져옵니다. */
    private Optional<Account> getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails    userDetails    = (UserDetails) authentication.getPrincipal();
        return this.accountMapper
                .findByEmail(userDetails.getUsername());
    }

}
