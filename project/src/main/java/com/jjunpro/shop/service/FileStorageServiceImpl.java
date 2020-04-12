package com.jjunpro.shop.service;

import com.google.api.client.util.DateTime;
import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.exception.FileStorageException;
import com.jjunpro.shop.exception.MyFileNotFoundException;
import com.jjunpro.shop.mapper.FileStorageMapper;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.properties.FileStorageProperties;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.imagelib.ImageLib;
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
            /* 파일을 저장할 폴더를 생성합니다. */
            Files.createDirectories(
                    this.fileStorageLocation.resolve(DomainType.ACCOUNT.getValue()));
            Files.createDirectories(
                    this.fileStorageLocation.resolve(DomainType.PRODUCT.getValue()));
        } catch (Exception ex) {
            throw new FileStorageException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public Long storeFile(MultipartFile file, DomainType domain) {
        // Normalize file name
        String fileOriName = StringUtils
                .cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileType = fileOriName.substring(fileOriName.lastIndexOf(".")).replace(".", "");

        String fileName      = this.setFileName(fileOriName);
        String fileNameThumb = this.setFileName("thumb-" + fileOriName);

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            /* 대상 위치로 파일 복사(같은 이름으로 기존 파일 바꾸기) */
            Path targetLocation = this.fileStorageLocation
                    .resolve(domain.getValue())
                    .resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            /*
             * 썸네일 생성
             *
             * resizeWidth, resizeHeight 줄이는 이미지 크기
             * resizeContent 이미지 이름에 들어가는 사이즈 크기 문자열
             */
            int resizeWidth  = 600;
            int resizeHeight = 600;

            BufferedImage resizeImage = ImageLib.handleThumbnail(
                    file.getBytes(),
                    resizeWidth,
                    resizeHeight
            );

            /* Bufferedimage to Inputstream */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(
                    resizeImage,
                    fileType,
                    os
            );
            InputStream thumbnail = new ByteArrayInputStream(os.toByteArray());

            Path targetLocationThumb = this.fileStorageLocation
                    .resolve(domain.getValue())
                    .resolve(fileNameThumb);

            Files.copy(
                    thumbnail,
                    targetLocationThumb,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/file/")
                    .path("/" + domain.getValue() + "/")
                    .path(fileNameThumb)
                    .toUriString();

            FileStorage fileStorage = FileStorage.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileDownloadUri(fileDownloadUri)
                    .build();

            this.fileStorageMapper.insert(fileStorage);

            return fileStorage.getId();
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    @Override
    public List<Long> uploadMultipleFiles(
            MultipartFile[] files,
            DomainType domain
    ) {
        /* 서버로 받은 파일'들'을 List로 변환하여 하나씩 서버로 업로드 합니다. */
        assert files != null;
        return Arrays
                .stream(files)
                .map(file -> this.storeFile(file, domain))
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
        return this.fileStorageMapper.findById(id);
    }

    @Override
    public void delete(String[] deleteFileArr, DomainType domainType) {
        for (String deleteFile : deleteFileArr) {
            try {
                Optional<FileStorage> dbFileStorage = this.fileStorageMapper
                        .findById(Long.parseLong(deleteFile.trim()));

                if (dbFileStorage.isPresent()) {
                    /* 원본파일 삭제 */
                    Path filePath = this.fileStorageLocation
                            .resolve(domainType.getValue())
                            .resolve(dbFileStorage.get().getFileName()).normalize();
                    Files.delete(filePath);

                    /* 썸네일 삭제 */
                    Path filePathThumb = this.fileStorageLocation
                            .resolve(domainType.getValue())
                            .resolve("thumb-" + dbFileStorage.get().getFileName()).normalize();
                    Files.delete(filePathThumb);

                    this.fileStorageMapper.delete(dbFileStorage.get().getId());
                }
            } catch (MalformedURLException ex) {
                throw new MyFileNotFoundException("File not found", ex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String setFileName(String fileOriName) {
        long             timeMillis       = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String           currentTime      = simpleDateFormat.format(timeMillis);

        String fileName = fileOriName.substring(0, fileOriName.lastIndexOf("."));
        String fileType = fileOriName.substring(fileOriName.lastIndexOf("."));

        return fileName + '-' + currentTime + fileType;
    }
}
