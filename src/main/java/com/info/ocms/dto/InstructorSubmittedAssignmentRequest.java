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
public class InstructorSubmittedAssignmentRequest {

    private Long id;
    private String grade;
    private String feedBack;

}
