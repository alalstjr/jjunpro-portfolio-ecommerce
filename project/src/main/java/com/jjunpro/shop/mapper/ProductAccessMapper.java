package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.ProductAccess;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductAccessMapper {

    void insert(ProductAccess productAccess);

    List<Long> findProductIdByAgeRange(byte ageRange);
}
