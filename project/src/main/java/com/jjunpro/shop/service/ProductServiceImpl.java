package com.jjunpro.shop.service;

import com.jjunpro.shop.mapper.FileStorageMapper;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.mapper.ShopGroupMapper;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ShopGroup;
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
    private final FileStorageMapper fileStorageMapper;

    @Override
    public void set(Product product) {
        /* 저장하는 파일이 없는경우 null 저장 */
        if (product.getFileStorageIds().isEmpty()) {
            product.setFileStorageIds(null);
        }

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

    /*
     * ignore 전달인자는 쿼리검색시 조건을 무시하는지 확인하는 변수
     * ex) 관리자 페이지인 경우 enabled 조건은 무시해서 불러와야 하기 때문
     */
    @Override
    public List<Product> findAll(Boolean ignore) {
        List<Product> productList;

        if (ignore) {
            productList = productMapper.findAllAdmin();
        } else {
            productList = productMapper.findAll();
        }

        Iterator<Product> iterator = productList.iterator();

        while (iterator.hasNext()) {
            Product product = iterator.next();

            if (product.getShopGroupIds() != null) {
                String[] groupIdArr = product.getShopGroupIds().split(",");
                Arrays.sort(groupIdArr);

                for (String groupId : groupIdArr) {
                    ShopGroup shopGroup = shopGroupMapper
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

                this.imageSet(product);
            }
        }

        /* 상품 목록에서 보려주는 대문 이미지 탐색 */

        return productList;
    }

    @Override
    public Optional<Product> findById(Long id) {
        Optional<Product> product = productMapper.findById(id);
        product.ifPresent(this::imageSet);

        return product;
    }

    @Override
    public Integer findCountByShopGroupId(Long shopGroupId) {
        return productMapper.findCountByShopGroupId(shopGroupId.toString());
    }

    /* 상품 목록에서 보려주는 썸네일 이미지 처리 */
    private void imageSet(Product product) {
        String[] fileStorageArr = product.getFileStorageIds().split(",");
        Optional<FileStorage> fileStorage = this.fileStorageMapper
                .findById(Long.parseLong(fileStorageArr[0].trim()));

        if (fileStorage.isPresent()) {
            String fileDownloadUri = fileStorage.get().getFileDownloadUri();
            product.setThumbnail(fileDownloadUri);
        }
    }
}
