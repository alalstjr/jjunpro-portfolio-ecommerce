package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.ShopGroupMapper;
import com.jjunpro.shop.model.ShopGroup;
import java.util.List;
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
    public Long set(ShopGroup shopGroup) {

        /* id 값을 기준으로 데이터쿼리 조작 */
        if(shopGroup.getId() == null) {
            shopGroup.setEnabled(true);
            shopGroupMapper.insert(shopGroup);
        } else{
            shopGroupMapper.update(shopGroup);
        }

        return shopGroup.getId();
    }

    @Override
    public void delete(Long id) {
        Optional<ShopGroup> byparentShopGroupId = shopGroupMapper.findByparentShopGroupId(id);

        if(byparentShopGroupId.isPresent()) {
            try {
                throw new Exception("해당 분류의 하위분류가 존재해서 삭제할 수 없습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        shopGroupMapper.delete(id);
    }

    @Override
    public List<ShopGroup> getAll() {
        return shopGroupMapper.getAll();
    }

    @Override
    public ShopGroup findById(Long id) {
        return shopGroupMapper.findById(id);
    }
}
