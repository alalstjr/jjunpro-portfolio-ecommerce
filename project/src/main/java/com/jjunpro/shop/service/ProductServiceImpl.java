package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public void set(Product product) {
        if(product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.update(product);
        }
    }

    @Override
    public void delete(Long id) {
        productMapper.delete(id);
    }
}
