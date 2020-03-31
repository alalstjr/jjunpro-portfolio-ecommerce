package com.jjunpro.shop.service;

import com.jjunpro.shop.model.ShopGroup;

public interface ShopGroupService {

    Long insertShopGroup(ShopGroup shopGroup);

    void deleteShopGroup(Long id);
}
