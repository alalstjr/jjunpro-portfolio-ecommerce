package com.jjunpro.shop.service;

import com.jjunpro.shop.model.ProductOrder;
import java.util.Optional;

public interface ProductOrderService {

    ProductOrder set(ProductOrder productOrder);

    Optional<ProductOrder> findById(Long id);

    String orderCancel(Long id);
}
