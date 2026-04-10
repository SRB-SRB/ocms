package com.info.ocms.service;

import com.info.ocms.dto.InstructorSubmittedAssignmentRequest;
import com.info.ocms.dto.StudentSubmittedAssignmentRequest;
import com.info.ocms.dto.SubmittedAssignmentResponse;
import com.info.ocms.dto.UpdateStudentSubmittedAssignmentRequest;

import java.io.IOException;

public interface SubmittedAssignmentService {
    SubmittedAssignmentResponse submitAssignment(StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest) throws IOException;

    SubmittedAssignmentResponse gradeAssignment(InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest);
    SubmittedAssignmentResponse getSubmittedAssignmentById(Long id);

    SubmittedAssignmentResponse updateSubmittedAssignment(UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest) throws IOException;
}
