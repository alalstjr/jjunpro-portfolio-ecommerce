package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.FileStorageMapper;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.mapper.ShopGroupMapper;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.util.FileUtil;
import com.jjunpro.shop.util.StringBuilderUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper     productMapper;
    private final ShopGroupMapper   shopGroupMapper;
    private final StringBuilderUtil stringBuilderUtil;
    private final FileUtil          fileUtil;

    @Override
    public void set(Product product) {
        /* 저장하는 파일이 없는경우 null 저장 */
        if (product.getFileStorageIds().isEmpty()) {
            product.setFileStorageIds(null);
        }

        String dataClassification = this.stringBuilderUtil
                .classifyData(product.getShopGroupIds());
        product.setShopGroupIds(dataClassification);

        if (product.getId() == null) {
            this.productMapper.insert(product);
        } else {
            this.productMapper.update(product);
        }
    }

    @Override
    public String delete(Long id) {
        this.productMapper.delete(id);

        return "삭제 완료";
    }

    /*
     * ignore 전달인자는 쿼리검색시 조건을 무시하는지 확인하는 변수
     * ex) 관리자 페이지인 경우 enabled 조건은 무시해서 불러와야 하기 때문
     */
    @Override
    public List<Product> findAll(Boolean ignore) {
        List<Product> productList;

        if (ignore) {
            productList = this.productMapper.findAllAdmin();
        } else {
            productList = this.productMapper.findAll();
        }

        Iterator<Product> iterator = productList.iterator();

        while (iterator.hasNext()) {
            Product product = iterator.next();

            if (product.getShopGroupIds() != null) {

                String[] groupIdArr = this.stringBuilderUtil
                        .classifyUnData(product.getShopGroupIds());

                for (String groupId : groupIdArr) {
                    ShopGroup shopGroup = this.shopGroupMapper
                            .findById(Long.parseLong(groupId));

                    product.getShopGroupList().add(shopGroup);

                    /*
                     * 상품의 분류가 판매 불가 설정확인
                     * ex) 상품은 판매 가능하지만 분류에서 판매 불가인경우를 나눕니다.
                     */
                    if (!shopGroup.getEnabled() && !ignore) {
                        iterator.remove();
                        break;
                    }
                }

                /* 상품의 썸네일을 지정합니다. */
                product.setThumbnail(this.fileUtil.thumbnailSet(product.getFileStorageIds()));
            }
        }

        /* 상품 목록에서 보려주는 대문 이미지 탐색 */
        return productList;
    }

    @Override
    public Optional<Product> findById(Long id) {
        Optional<Product> product = this.productMapper.findById(id);
        product.ifPresent(this.fileUtil::fileSet);

        return product;
    }

    @Override
    public Integer findCountByShopGroupId(Long shopGroupId) {
        return this.productMapper.findCountByShopGroupId(shopGroupId.toString());
    }

    @Override
    public List<Product> findByShopGroupId(Long id) {
        List<Product> dbProductList = this.productMapper.findByShopGroupId(id);

        for (Product product : dbProductList) {
            product.setThumbnail(this.fileUtil.thumbnailSet(product.getFileStorageIds()));
        }

        return dbProductList;
    }
}
