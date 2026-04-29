package com.info.ocms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMasterResponse {
    private Long id;
    private String documentGuid;
    private String fileExtension;
    private String mimeType;
    private String fileName;
    private String documentType;
   private String filePath;

}
