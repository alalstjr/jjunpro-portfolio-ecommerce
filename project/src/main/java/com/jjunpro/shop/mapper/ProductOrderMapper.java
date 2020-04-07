package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.ProductOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductOrderMapper {

    void insert(ProductOrder productOrder);
}
