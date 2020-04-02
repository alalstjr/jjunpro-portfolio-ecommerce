package com.jjunpro.shop.service;

import com.jjunpro.shop.model.ShopGroup;
import java.util.List;

public interface ShopGroupService {

    Long set(ShopGroup shopGroup);

    void delete(Long id);

    List<ShopGroup> getAll();

    ShopGroup findById(Long id);
}
