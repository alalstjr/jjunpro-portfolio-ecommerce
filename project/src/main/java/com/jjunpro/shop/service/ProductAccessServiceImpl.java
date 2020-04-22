package com.jjunpro.shop.service;

import com.jjunpro.shop.dto.ProductAccessAgeDTO;
import com.jjunpro.shop.dto.ProductAccessDTO;
import com.jjunpro.shop.mapper.ProductAccessMapper;
import com.jjunpro.shop.mapper.ProductMapper;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ProductAccess;
import com.jjunpro.shop.util.FileUtil;
import com.jjunpro.shop.util.StringBuilderUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAccessServiceImpl implements ProductAccessService {

    private DateTime         checkTime        = null;
    private ProductAccessDTO productAccessDTO = new ProductAccessDTO();

    private final StringBuilderUtil   stringBuilderUtil;
    private final FileUtil            fileUtil;
    private final ProductAccessMapper productAccessMapper;
    private final ProductMapper       productMapper;

    @Override
    public void set(ProductAccess productAccess) {
        this.productAccessMapper.insert(productAccess);
    }

    @Override
    public ProductAccessAgeDTO getAgeAccessByProduct() {
        this.initTime();

        return ProductAccessAgeDTO.builder()
                .ageTen(this.getProduct(this.productAccessDTO.getAgeTen(), true))
                .ageTwenty(this.getProduct(this.productAccessDTO.getAgeTwenty(), true))
                .ageThirty(this.getProduct(this.productAccessDTO.getAgeThirty(), true))
                .ageForty(this.getProduct(this.productAccessDTO.getAgeForty(), true))
                .ageEtc(this.getProduct(this.productAccessDTO.getAgeEtc(), true))
                .build();
    }

    @Override
    public List<Product> getAgeAccessByProductUser(byte ageRange) {
        this.initTime();

        switch (ageRange) {
            case (byte) 10:
                return this.getProduct(this.productAccessDTO.getAgeTen(), false);
            case (byte) 20:
                return this.getProduct(this.productAccessDTO.getAgeTwenty(), false);
            case (byte) 30:
                return this.getProduct(this.productAccessDTO.getAgeThirty(), false);
            case (byte) 40:
                return this.getProduct(this.productAccessDTO.getAgeForty(), false);
            default:
                return this.getProduct(this.productAccessDTO.getAgeEtc(), false);
        }
    }

    private void initTime() {
        DateTime dateTime = new DateTime();

        /* 초기 캐싱 설정 */
        if (this.checkTime == null) {
            /* 캐싱된 시간 설정 */
            this.setTimeAndData(dateTime);
        }

        /* 캐싱 1분이 지난경우 DB 재탐색 입니다.. 만 포트폴리오 설정으로 바로바로 업데이트 */
        // if (dateTime.getMillis() > this.checkTime.getMillis() + (60 * 1000)) {
        /* 캐싱된 시간 설정 */
        this.setTimeAndData(dateTime);
    }


    private void setTimeAndData(DateTime dateTime) {
        this.checkTime = dateTime;

        String ageTen = this
                .getDataList(productAccessMapper.findProductIdByAgeRange((byte) 10));
        String ageTwenty = this
                .getDataList(productAccessMapper.findProductIdByAgeRange((byte) 20));
        String ageThirty = this
                .getDataList(productAccessMapper.findProductIdByAgeRange((byte) 30));
        String ageForty = this
                .getDataList(productAccessMapper.findProductIdByAgeRange((byte) 40));
        String ageEtc = this
                .getDataList(productAccessMapper.findProductIdByAgeRange((byte) 0));

        this.productAccessDTO = ProductAccessDTO.builder()
                .ageTen(ageTen)
                .ageTwenty(ageTwenty)
                .ageThirty(ageThirty)
                .ageForty(ageForty)
                .ageEtc(ageEtc)
                .build();
    }

    private String getDataList(List<Long> values) {
        StringBuilder result = new StringBuilder();

        if (values.size() > 0) {
            Long                   temp      = values.get(0);
            int                    tempCount = 1;
            HashMap<Long, Integer> hashMap   = new HashMap<>();

            /* 탐색하기 전 정렬 */
            values.sort(null);

            int i = 0;
            for (Long value : values) {
                if (!temp.equals(value)) {
                    hashMap.put(temp, tempCount - 1);
                    temp = value;
                    tempCount = 1;
                }

                if (i == values.size() - 1) {
                    hashMap.put(value, tempCount);
                    tempCount = 1;
                    continue;
                }

                tempCount++;
                i++;
            }

            /*System.out.println(hashMap.toString());*/

            /* 결과값 HashMap sort 내림차순 정렬 */
            List<Long> keySetList = new ArrayList<>(hashMap.keySet());
            keySetList.sort((o1, o2) -> (hashMap.get(o2).compareTo(hashMap.get(o1))));

            for (Long key : keySetList) {
                /*System.out.println("key : " + key + " / " + "value : " + hashMap.get(key));*/
                result.append(key);
                result.append(",");
            }
        }

        return result.toString();
    }

    private List<Product> getProduct(String productId, Boolean ignore) {
        List<Product> productList = new ArrayList<>();

        if (productId != null) {
            if (!productId.isEmpty()) {
                /* 탐색된 상품의 id 를 가지고 상품을 조회 데이터를 가공합니다. */
                String[] idArr = stringBuilderUtil.classifyUnData(productId);

                int i = 0;
                for (String id : idArr) {
                    Optional<Product> dbProduct;
                    if (ignore) {
                        dbProduct = productMapper.findByIdAdmin(Long.parseLong(id));
                    } else {
                        dbProduct = productMapper.findById(Long.parseLong(id));
                    }

                    if (dbProduct.isPresent()) {
                        dbProduct.get().setThumbnail(
                                this.fileUtil.thumbnailSet(dbProduct.get().getFileStorageIds())
                        );
                        productList.add(dbProduct.get());
                    }

                    /* 상품을 가져오는 최대 5개 까지만 가져옵니다. */
                    if (i == 5) {
                        break;
                    }
                    i++;
                }
            }
        }

        return productList;
    }
}
