package com.jjunpro.shop.service;

import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.model.FileStorage;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    Long storeFile(MultipartFile file, DomainType domain);

    /* Local file 생성 */
    Long storeResource(Resource resource, DomainType domain);

    /* 외부 링크 URL file 생성 */
    Long storeResource(String fileUrl, DomainType domain);

    List<Long> uploadMultipleFiles(MultipartFile[] files, DomainType domain);

    Resource loadFileAsResource(String fileName);

    Optional<FileStorage> findById(Long id);

    void delete(String[] deleteFileArr, DomainType domainType);
}
