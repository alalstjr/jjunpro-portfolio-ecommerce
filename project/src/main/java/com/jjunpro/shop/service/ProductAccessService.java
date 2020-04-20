package com.jjunpro.shop.service;

import com.jjunpro.shop.dto.ProductAccessAgeDTO;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductAccess;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ProductAccessService {

    void set(ProductAccess productAccess);

    ProductAccessAgeDTO getAgeAccessByProduct();

    List<Product> getAgeAccessByProductUser(byte ageRange);
}
