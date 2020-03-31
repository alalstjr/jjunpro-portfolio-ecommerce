package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.ShopGroup;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopGroupMapper {

    Long insertShopGroup(ShopGroup shopGroup);

    void deleteShopGroup(Long id);

    Optional<ShopGroup> findByparentShopGroupId(Long id);
}
