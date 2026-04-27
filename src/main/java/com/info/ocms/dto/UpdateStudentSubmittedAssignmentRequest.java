package com.info.ocms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentSubmittedAssignmentRequest {

    private Long id;
    private List<MultipartFile> submittedAssignmentFiles=new ArrayList<>();
    private List<FileResponse> existingFiles;
    private List<Long> keepFilesIds=new ArrayList<>();
}
