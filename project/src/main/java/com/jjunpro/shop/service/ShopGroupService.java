package com.jjunpro.shop.service;

import com.jjunpro.shop.model.ShopGroup;
import java.util.List;

public interface ShopGroupService {

    Long set(ShopGroup shopGroup);

    String delete(Long id);

    List<ShopGroup> findByIsNullParentShopGroupId();

    ShopGroup findById(Long id);

    Integer allCount();
}
