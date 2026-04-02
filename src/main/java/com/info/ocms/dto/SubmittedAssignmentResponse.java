package com.info.ocms.dto;

import com.info.ocms.model.Assignment;
import com.info.ocms.model.SubmittedAssignmentFile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedAssignmentResponse {
    private Long id;
    private String assignmentName;
    private String grade;
    private String feedBack;
    private List<FileResponse> submittedAssignmentFiles=new ArrayList<>();

}
