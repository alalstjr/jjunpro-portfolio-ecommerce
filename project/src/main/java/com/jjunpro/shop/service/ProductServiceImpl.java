package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.mapper.ShopGroupMapper;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ShopGroup;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper   productMapper;
    private final ShopGroupMapper shopGroupMapper;

    @Override
    public void set(Product product) {
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.update(product);
        }
    }

    @Override
    public String delete(Long id) {
        productMapper.delete(id);

        return "삭제 완료";
    }

    @Override
    public List<Product> findAll() {

        List<Product> productList = productMapper.findAll();

        for (Product product : productList) {
            if (product.getShopGroupIds() != null) {
                String[] groupIdArr = product.getShopGroupIds().split(",");

                for (String groupId : groupIdArr) {
                    ShopGroup shopGroup = shopGroupMapper
                            .findById(Long.parseLong(groupId));

                    product.getShopGroupList().add(shopGroup);
                }
            }
        }

        return productList;
    }

    @Override
    public Product findById(Long id) {
        return productMapper.findById(id);
    }
}
