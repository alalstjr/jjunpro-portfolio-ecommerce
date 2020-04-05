package com.jjunpro.shop.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStorage {

    private Long          id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String        fileName;
    private String        fileDownloadUri;
    private String        fileType;
    private Long          fileSize;
}
