package com.jjunpro.shop.service;

import com.jjunpro.shop.model.Product;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    void set(Product product);

    String delete(Long id);

    List<Product> findAll();

    Product findById(Long id);
}
