package com.info.ocms.service;

import com.info.ocms.dto.DocumentMasterResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentMasterService {

    DocumentMasterResponse createFile(MultipartFile file, String documentType) throws IOException;
    DocumentMasterResponse getByDocumentGuide(String documentGuide);
    void deleteByDocumentGuide(String documentGuide);

}
