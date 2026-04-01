package com.info.ocms.service.serviceImpl;

import com.info.ocms.dto.DocumentMasterResponse;
import com.info.ocms.exception.FileSizeExceededException;
import com.info.ocms.model.DocumentMaster;
import com.info.ocms.ropository.DocumentMasterRepo;
import com.info.ocms.service.DocumentMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentMasterServiceImpl implements DocumentMasterService {
    private final DocumentMasterRepo documentMasterRepo;
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public DocumentMasterResponse createFile(MultipartFile file, String documentType) throws  IOException{
        if(file.getSize()>2*1024*1024){
            throw new FileSizeExceededException("Flies size exceeds 2 MB");
        }
        DocumentMaster documentMaster=new DocumentMaster();
        documentMaster.setDocumentGuid(UUID.randomUUID().toString());
        documentMaster.setFileExtension(StringUtils.getFilenameExtension(file.getOriginalFilename()));
        documentMaster.setMimeType(file.getContentType());
        documentMaster.setUrl(saveFile(file));
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

        return filePath.toString();
    }
    private void deleteFile(String filePath){
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("File deleted: " + filePath);
            } else {
                System.out.println("File not found: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to delete file: " + filePath);
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
