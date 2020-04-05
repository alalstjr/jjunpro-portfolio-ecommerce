package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.FileStorage;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileStorageMapper {

    Long insert(FileStorage fileStorage);

    Optional<FileStorage> findById(Long id);

    void delete(Long id);
}
