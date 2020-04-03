package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.ShopGroup;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopGroupMapper {

    Long insert(ShopGroup shopGroup);

    Long update(ShopGroup shopGroup);

    void delete(Long id);

    Optional<ShopGroup> findOneByparentShopGroupId(Long id);

    List<ShopGroup> findByparentShopGroupIdList(Long id);

    List<ShopGroup> findByIsNullParentShopGroupId();

    ShopGroup findById(Long id);
}
