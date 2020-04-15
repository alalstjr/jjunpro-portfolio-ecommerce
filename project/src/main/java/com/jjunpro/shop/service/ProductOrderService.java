package com.jjunpro.shop.service;

import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductOrder;
import java.util.List;
import java.util.Optional;

public interface ProductOrderService {

    ProductOrder set(ProductOrder productOrder);

    Optional<ProductOrder> findById(Long id);

    List<ProductOrder> findByAccountIdList(Long accountId);

    String orderCancel(Long id);

    Integer findTotalAmountByOrderState();

    Integer findCountByAll();

    Integer findCountByOrderState();

    List<ProductOrder> findAllAdmin();

    void updateOrderStateById(Long id, short orderState);
}
