package com.jjunpro.shop.service;

import com.jjunpro.shop.model.Product;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    void set(Product product);

    void delete(Long id);
}
