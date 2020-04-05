package com.jjunpro.shop.service;

import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.exception.FileStorageException;
import com.jjunpro.shop.exception.MyFileNotFoundException;
import com.jjunpro.shop.mapper.FileStorageMapper;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.properties.FileStorageProperties;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@Transactional
public class FileStorageServiceImpl implements FileStorageService {

    private final Path              fileStorageLocation;
    private final FileStorageMapper fileStorageMapper;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties,
            FileStorageMapper fileStorageMapper) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        this.fileStorageMapper = fileStorageMapper;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public Long storeFile(MultipartFile file, DomainType domain) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    //.path("/" + domain.getValue() + "/")
                    .path("/downloadFile/")
                    .path(fileName)
                    .toUriString();

            FileStorage fileStorage = FileStorage.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileDownloadUri(fileDownloadUri)
                    .build();

            fileStorageMapper.insert(fileStorage);

            return fileStorage.getId();
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    @Override
    public List<Long> uploadMultipleFiles(
            MultipartFile[] files
    ) {
        /* 서버로 받은 파일'들'을 List로 변환하여 하나씩 서버로 업로드 합니다. */
        assert files != null;
        return Arrays
                .stream(files)
                .map(file -> this.storeFile(file, DomainType.PRODUCT))
                .collect(Collectors.toList());
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path     filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public Optional<FileStorage> findById(Long id) {
        return fileStorageMapper.findById(id);
    }

    @Override
    public void delete(String[] deleteFileArr) {
        for (String deleteFile : deleteFileArr) {
            try {
                Optional<FileStorage> dbFileStorage = fileStorageMapper
                        .findById(Long.parseLong(deleteFile));

                if (dbFileStorage.isPresent()) {
                    Path filePath = this.fileStorageLocation
                            .resolve(dbFileStorage.get().getFileName()).normalize();
                    Files.delete(filePath);

                    fileStorageMapper.delete(dbFileStorage.get().getId());
                }
            } catch (MalformedURLException ex) {
                throw new MyFileNotFoundException("File not found", ex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
