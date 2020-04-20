package com.jjunpro.shop.service;

import com.jjunpro.shop.common.CloudStorageHelper;
import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.exception.FileStorageException;
import com.jjunpro.shop.exception.MyFileNotFoundException;
import com.jjunpro.shop.mapper.FileStorageMapper;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.properties.FileStorageProperties;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import lombok.RequiredArgsConstructor;
import org.imagelib.ImageLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

//@Service
//@Transactional
//@RequiredArgsConstructor
public class FileStorageGoogleServiceImpl implements FileStorageService {

    /* Google Cloud */
    @Value("${google.id}")
    private String             _GCSID;
    private CloudStorageHelper cloudStorageHelper;

    /* Local Cloud */
    private FileStorageMapper fileStorageMapper;

    /* MultipartFile file 전용 메소드 주로 새로운 파일을 업로드 할때 처리 */
    @Override
    public Long storeFile(MultipartFile file, DomainType domain) {
        String fileOriName = StringUtils
                .cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileType = fileOriName.substring(fileOriName.lastIndexOf(".")).replace(".", "");

        String fileName      = domain.getValue() + '/' + this.setFileName(fileOriName);
        String fileNameThumb = domain.getValue() + '/' + this.setFileName("thumb-" + fileOriName);

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

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

            FileStorage fileStorage = FileStorage.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileDownloadUri(fileNameThumb)
                    .build();

            this.fileStorageMapper.insert(fileStorage);

            /* GCS Upload 원본 이미지파일 저장 */
            try {
                cloudStorageHelper.uploadFile(
                        file.getInputStream(),
                        fileName,
                        _GCSID
                );
                cloudStorageHelper.uploadFile(
                        thumbnail,
                        fileNameThumb,
                        _GCSID
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            return fileStorage.getId();
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    /*
     * Resource file 전용 메소드 주로 이미 업로드된 파일의 URI 파악하려 복사 업로드 할때 처리
     * ex) product img -> productOrder img 복사 업로드
     */
    @Override
    public Long storeResource(String fileUrl, DomainType domain) {
        File file = null;
        URL  url  = null;

        /*
         * 외부 URL 링크파일 생성
         * 도움받은 링크 : https://stackoverflow.com/questions/8324862/how-to-create-file-object-from-url-object
         */
        try {
            url = new URL(fileUrl);
            BufferedImage img = ImageIO.read(url);
            file = new File("downloaded.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Normalize file name
        String fileOriName = StringUtils.cleanPath(Objects.requireNonNull(
                Objects.requireNonNull(file).getName()));
        String fileType      = fileOriName.substring(fileOriName.lastIndexOf(".")).replace(".", "");
        String fileThumbName = "thumb." + fileType;

        String fileName = domain.getValue() + '/' + this.setFileName(fileThumbName);

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            /*
             * 썸네일 생성
             *
             * resizeWidth, resizeHeight 줄이는 이미지 크기
             * resizeContent 이미지 이름에 들어가는 사이즈 크기 문자열
             */
            int resizeWidth  = 100;
            int resizeHeight = 100;

            BufferedImage resizeImage = ImageLib.handleThumbnail(
                    url.openStream().readAllBytes(),
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

            /* 대상 위치로 파일 복사(같은 이름으로 기존 파일 바꾸기) */
            InputStream thumbnail = new ByteArrayInputStream(os.toByteArray());

            FileStorage fileStorage = FileStorage.builder()
                    .fileName(fileName)
                    .fileType(fileType)
                    .fileSize((long) os.size())
                    .fileDownloadUri(fileName)
                    .build();

            this.fileStorageMapper.insert(fileStorage);

            try {
                cloudStorageHelper.uploadFile(
                        thumbnail,
                        fileName,
                        _GCSID
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            return fileStorage.getId();
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    @Override
    public Long storeResource(Resource resource, DomainType domain) {
        return null;
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
            Resource resource = new UrlResource(fileName);

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
            Optional<FileStorage> dbFileStorage = this.fileStorageMapper
                    .findById(Long.parseLong(deleteFile.trim()));

            if (dbFileStorage.isPresent()) {
                // GCS File Delete
                cloudStorageHelper.deleteFile(
                        dbFileStorage.get().getFileName(),
                        _GCSID
                );
                cloudStorageHelper.deleteFile(
                        dbFileStorage.get().getFileDownloadUri(),
                        _GCSID
                );

                this.fileStorageMapper.delete(dbFileStorage.get().getId());
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
