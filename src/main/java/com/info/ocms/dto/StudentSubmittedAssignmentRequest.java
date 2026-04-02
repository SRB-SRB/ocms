package com.info.ocms.dto;

import com.info.ocms.model.Assignment;
import com.info.ocms.model.SubmittedAssignmentFile;
import jakarta.persistence.*;
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
public class StudentSubmittedAssignmentRequest {



    private List<MultipartFile> submittedAssignmentFiles=new ArrayList<>();
    private Long assignmentId;
}
