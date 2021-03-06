package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.Product;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {

    void insert(Product product);

    void update(Product product);

    void delete(Long id);

    List<Product> findAllAdmin();

    List<Product> findAll();

    Optional<Product> findByIdAdmin(Long id);

    Optional<Product> findById(Long id);

    Integer findCountByShopGroupId(String shopGroupId);

    void updateQuantity(Long id, Integer afterQuantity);

    List<Product> findByShopGroupId(Long id);
}
