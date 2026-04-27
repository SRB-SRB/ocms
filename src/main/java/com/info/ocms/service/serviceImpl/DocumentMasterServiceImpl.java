package com.info.ocms.service.serviceImpl;

import com.info.ocms.dto.DocumentMasterResponse;
import com.info.ocms.exception.FileSizeExceededException;
import com.info.ocms.model.DocumentMaster;
import com.info.ocms.ropository.DocumentMasterRepo;
import com.info.ocms.service.DocumentMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentMasterServiceImpl implements DocumentMasterService {
    private final DocumentMasterRepo documentMasterRepo;
    @Value("${app.upload.dir}")
    private String uploadDir;
    @Value("${app.upload.max-size-bytes}")
    private long maxSize;


    private static final List<String> ALLOWED_EXTENSIONS=List.of("pdf","doc","docx","ppt","pptx","xls","xlsx","jpg","jpeg","png","mp4","zip");

    @Override
    public DocumentMasterResponse createFile(MultipartFile file, String documentType) throws  IOException{
        if(file.getSize()>maxSize){
            throw new FileSizeExceededException("File size exceed 2-MB");
        }

        String extension=StringUtils.getFilenameExtension(file.getOriginalFilename());
        if(extension==null||!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())){
            throw new RuntimeException("File Type Not Allowed: "+extension);
        }

        DocumentMaster documentMaster=new DocumentMaster();
        documentMaster.setDocumentGuid(UUID.randomUUID().toString());
        documentMaster.setFileExtension(StringUtils.getFilenameExtension(file.getOriginalFilename()));
        documentMaster.setMimeType(file.getContentType());
        documentMaster.setUrl(saveFile(file));
        documentMaster.setFileSize(file.getSize());
        documentMaster.setFileName(file.getOriginalFilename());
        documentMaster.setDocumentType(documentType);
       return mapToFileResponse(documentMasterRepo.save(documentMaster)) ;

    }

    @Override
    public DocumentMasterResponse getByDocumentGuide(String documentGuide) {
       return mapToFileResponse( documentMasterRepo.findByDocumentGuid(documentGuide).orElseThrow(()->new RuntimeException("FILE NOT FOUND")));
    }

    @Override
    public void deleteByDocumentGuide(String documentGuide) {
        DocumentMaster documentMaster=documentMasterRepo.findByDocumentGuid(documentGuide).orElseThrow(()->new RuntimeException("File Not Found"));
        deleteFile(documentMaster.getUrl());
        documentMasterRepo.deleteByDocumentGuid(documentGuide);
    }


    private String saveFile(MultipartFile file) throws IOException {

        Path uploadPath= Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        String fileName=UUID.randomUUID().toString();
        Path filePath=uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(),filePath);
       log.info("File saved: {}", filePath);
        return filePath.toString();
    }
    private void deleteFile(String filePath){
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
               log.info("File deleted: {}",filePath);
            } else {
                log.warn("File not found for deletion: {}",filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath,e);
        }
    }
    private DocumentMasterResponse mapToFileResponse(DocumentMaster documentMaster){
        DocumentMasterResponse documentMasterResponse= new DocumentMasterResponse();
        documentMasterResponse.setId(documentMaster.getId());
        documentMasterResponse.setDocumentGuid(documentMaster.getDocumentGuid());
        documentMasterResponse.setFileExtension(documentMaster.getFileExtension());
        documentMasterResponse.setFileName(documentMaster.getFileName());
        documentMasterResponse.setDocumentType(documentMaster.getDocumentType());
        return documentMasterResponse;
    }

}
