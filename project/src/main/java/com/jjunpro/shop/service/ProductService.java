package com.jjunpro.shop.service;

import com.jjunpro.shop.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    void set(Product product);

    String delete(Long id);

    List<Product> findAll(Boolean ignore);

    Optional<Product> findById(Long id, Boolean ignore);

    Integer findCountByShopGroupId(Long shopGroupId);

    List<Product> findByShopGroupId(Long id);
}
