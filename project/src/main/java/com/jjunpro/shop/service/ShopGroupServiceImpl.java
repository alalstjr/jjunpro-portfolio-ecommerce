package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.mapper.ShopGroupMapper;
import com.jjunpro.shop.model.ShopGroup;
import java.util.Arrays;
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
    private final ProductMapper   productMapper;

    @Override
    public Long set(ShopGroup shopGroup) {

        /* id 값을 기준으로 데이터쿼리 조작 */
        if (shopGroup.getId() == null) {
            shopGroupMapper.insert(shopGroup);
        } else {
            shopGroupMapper.update(shopGroup);
        }

        return shopGroup.getId();
    }

    @Override
    public String delete(Long id) {
        Optional<ShopGroup> byparentShopGroupId = shopGroupMapper.findOneByparentShopGroupId(id);
        Integer countByShopGroupId = productMapper
                .findCountByShopGroupId(id.toString());

        if (countByShopGroupId > 0) {
            return "해당 분류에 상품이 존재해서 삭제할 수 없습니다.";
        }

        if (byparentShopGroupId.isPresent()) {
            return "해당 분류의 하위분류가 존재해서 삭제할 수 없습니다.";
        }

        shopGroupMapper.delete(id);

        return "삭제 완료";
    }

    @Override
    public List<ShopGroup> findByIsNullParentShopGroupId() {

        /* 시작은 대분류 목록을 조회 */
        List<ShopGroup> shopGroupList = shopGroupMapper.findByIsNullParentShopGroupId();

        this.recursion(shopGroupList);

        return shopGroupList;
    }

    @Override
    public ShopGroup findById(Long id) {
        return shopGroupMapper.findById(id);
    }

    @Override
    public Integer allCount() {
        return shopGroupMapper.allCount();
    }

    /* 분류 호출 Recursion 재귀함수 */
    private void recursion(List<ShopGroup> shopGroupList) {
        /* 부모노드 분류의 자식노드 분류 List 를 조회 */
        for (ShopGroup shopGroup : shopGroupList) {
            /* 하위노드 분류 List */
            List<ShopGroup> childrenShopGroupList = shopGroupMapper
                    .findByparentShopGroupIdList(shopGroup.getId());

            /* 그룹이 포함된 상품의 갯수 탐색 */
            Integer shopGroupCount = productMapper
                    .findCountByShopGroupId(shopGroup.getId().toString());
            shopGroup.setProductCount(shopGroupCount);

            /* 조회된 자식노드가 존재하는경우 List 추가 */
            if (!childrenShopGroupList.isEmpty()) {
                shopGroup.getChildrenShopGroupList().addAll(childrenShopGroupList);

                this.recursion(childrenShopGroupList);
            }
        }
    }
}
