package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.ShopGroupMapper;
import com.jjunpro.shop.model.ShopGroup;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopGroupServiceImpl implements ShopGroupService {

    private final ShopGroupMapper shopGroupMapper;

    @Override
    public Long insertShopGroup(ShopGroup shopGroup) {
        shopGroup.setEnabled(true);
        shopGroupMapper.insertShopGroup(shopGroup);

        return shopGroup.getId();
    }

    @Override
    public void deleteShopGroup(Long id) {
        Optional<ShopGroup> byparentShopGroupId = shopGroupMapper.findByparentShopGroupId(id);

        if(byparentShopGroupId.isPresent()) {
            try {
                throw new Exception("해당 분류의 하위분류가 존재해서 삭제할 수 없습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        shopGroupMapper.deleteShopGroup(id);
    }
}
