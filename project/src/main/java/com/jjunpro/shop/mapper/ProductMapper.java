package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {

    void insert(Product product);

    void update(Product product);

    void delete(Long id);
}
