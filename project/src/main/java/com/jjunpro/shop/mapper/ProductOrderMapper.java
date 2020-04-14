package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.ProductOrder;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductOrderMapper {

    void insert(ProductOrder productOrder);

    Optional<ProductOrder> findById(Long id);

    void orderCancel(Long id);

    List<ProductOrder> findByAccountIdList(Long accountId);
}
