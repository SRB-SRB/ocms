package com.info.ocms.dto;

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
    private Long assignmentId;
    private String assignmentName;
    private String grade;
    private String feedBack;
    private Long userId;
    private String userName;
    private List<FileResponse> submittedAssignmentFiles=new ArrayList<>();

}
