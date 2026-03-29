package com.info.ocms.service;

import com.info.ocms.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentMasterService {

    FileResponse createFile(MultipartFile file,String documentType) throws IOException;
    FileResponse getByDocumentGuide(String documentGuide);
    void deleteByDocumentGuide(String documentGuide);

}
