package com.jjunpro.shop.util;

import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.service.FileStorageServiceImpl;
import java.lang.reflect.Field;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/*
 * 전달받는 Object dto 에는 필수로 존재해야 하는 Field 값이 있습니다.
 *
 * private MultipartFile[] fileStorage;
 * private String fileStorageIds;
 * private String deleteFileStorageIds;
 *
 * fileStorage = 클라이언트에서 전달받은 파일
 * fileStorageIds = { DB Data } 저장된 FileStorage id 값
 * deleteFileStorageIds = { DB Data } 에서 삭제 예정인 FileStorage id 값
 *
 * 해당 Field 값을 Util Field 로 전달 복사하여 처리하는 방식입니다.
 *
 * 리플렉션 도움을 받은 링크
 * https://www.rgagnon.com/javadetails/java-get-fields-and-values-from-an-object.html
 */
@Component
@RequiredArgsConstructor
public class FileUtil {

    private MultipartFile[] fileStorage;
    private String          fileStorageIds;
    private String          deleteFileStorageIds;

    private final FileStorageServiceImpl fileStorageService;

    private void setFieldHandler(Object obj) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz                     = obj.getClass();
        Field    fieldFileStorage          = clazz.getDeclaredField("fileStorage");
        Field    fieldFileStorageIds       = clazz.getDeclaredField("fileStorageIds");
        Field    fieldDeleteFileStorageIds = clazz.getDeclaredField("deleteFileStorageIds");

        fieldFileStorage.setAccessible(true);
        fieldFileStorageIds.setAccessible(true);
        fieldDeleteFileStorageIds.setAccessible(true);

        this.fileStorage = (MultipartFile[]) fieldFileStorage.get(obj);
        this.fileStorageIds = (String) fieldFileStorageIds.get(obj);
        this.deleteFileStorageIds = (String) fieldDeleteFileStorageIds.get(obj);
    }

    private void setFileStorageIds(Object obj, String value)
            throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz               = obj.getClass();
        Field    fieldFileStorageIds = clazz.getDeclaredField("fileStorageIds");

        fieldFileStorageIds.setAccessible(true);

        fieldFileStorageIds.set(obj, value);
    }

    public void setFileHandler(Object dto, DomainType domain)
            throws NoSuchFieldException, IllegalAccessException {
        this.setFieldHandler(dto);

        StringBuilder uploadFile = new StringBuilder();

        /*
         * - file remove
         */
        if (!this.deleteFileStorageIds.isEmpty()) {
            String[] dbFileArr     = {};
            String[] deleteFileArr = this.deleteFileStorageIds.split(",");

            /* 업로드없이 삭제만 있는경우 Null 체크 */
            if (this.fileStorageIds != null) {
                dbFileArr = this.fileStorageIds.split(",");
            }

            /*
             * 삭제하려는 파일 id 값은 저장하면 안되므로
             * { uploadFile } 변수에 담아서 전송할 수 없도록 합니다.
             *
             * 업로드된 파일 정보와 삭제하려는 파일을 비교합니다.
             * 삭제하려는 파일정보가 업로드된 파일정보와 일치하지 않으면
             * 서버로 전송 저장되는 { uploadFile } 변수에 담아집니다.
             *
             * ex) 전송하는 File : 1, 2, 3, 4, 5
             *     삭제하는 File : 1, 7, 3, 6, 8
             *
             * uploadFile = { 1, 3 } , 이외 값들은 삭제됩니다.
             *
             * 2중 for문에서 삭제되는 값 확인되면 break 문을 사용하여 불필요한 탐색을 안하도록 중지합니다.
             */
            for (String dbFile : dbFileArr) {
                boolean equalsCheck = true;
                for (String deleteFile : deleteFileArr) {
                    if (dbFile.trim().equals(deleteFile.trim())) {
                        equalsCheck = false;
                        break;
                    }
                }

                if (equalsCheck) {
                    uploadFile.append(dbFile.trim()).append(",");
                }
            }

            if (uploadFile.length() != 0) {
                /*
                 * uploadFile 변수 끝자리에 오는 문자 ',' 콤마를 삭제합니다.
                 * ex) { 1, 2, 3, } => { 1, 2, 3 }
                 */
                this.setFileStorageIds(dto, uploadFile.substring(0, uploadFile.length() - 1));
            }

            this.fileStorageService.delete(deleteFileArr, domain);
        }

        /*
         * - file upload
         *
         * 문자열로 저장하기전에 배열에 포함되는 특수문자 ([, ]) 가로를 삭제 후 저장해 줍니다.
         * 다른곳에서 불러와 문자열을 배열로 만들 때 따로 특수만자 제거 작업이 필요없도록 미리 설정하는 것입니다.
         */
        /* 업로드 파일이 하나 이상 존재하는 경우 */
        if (!this.fileStorage[0].isEmpty()) {
            /*
             * 기존에 저장된 파일 id 값이 존재하고
             * uploadFile.length() 값이 0 인경우 => file remove 과정을 거치지 않은 경우
             *
             * 기존의 저장된 파일 id 값과 새로 저장하려는 파일 id 값을 같이 { uploadFile } 저장하여 전송합니다.
             * ex) 기존 저장된 파일 id { 1, 2, 3 }
             *     새로 저장되는 파일 Id { 5, 6 }
             *     uploadFile => { 1, 2, 3, 5, 6 }
             */
            if (!this.fileStorageIds.isEmpty() && uploadFile.length() == 0) {
                uploadFile.append(this.fileStorageIds).append(",");
            }

            List<Long> longs = fileStorageService
                    .uploadMultipleFiles(this.fileStorage, domain);

            String replaceAll = longs.toString().replaceAll("[\\[\\]]", "");
            uploadFile.append(replaceAll);

            this.setFileStorageIds(dto, uploadFile.toString());
        }
    }
}
