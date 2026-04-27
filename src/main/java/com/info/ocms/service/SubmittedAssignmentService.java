package com.info.ocms.service;

import com.info.ocms.dto.InstructorSubmittedAssignmentRequest;
import com.info.ocms.dto.StudentSubmittedAssignmentRequest;
import com.info.ocms.dto.SubmittedAssignmentResponse;
import com.info.ocms.dto.UpdateStudentSubmittedAssignmentRequest;
import com.info.ocms.model.SubmittedAssignment;

import java.io.IOException;
import java.util.List;

public interface SubmittedAssignmentService {
    SubmittedAssignmentResponse submitAssignment(StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest) throws IOException;

    SubmittedAssignmentResponse gradeAssignment(InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest);
    SubmittedAssignmentResponse getSubmittedAssignmentById(Long id);
    List<SubmittedAssignmentResponse> getSubmissionsByAssignment(Long assignmentId);

    SubmittedAssignmentResponse updateSubmittedAssignment(UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest) throws IOException;
    List<SubmittedAssignmentResponse> getMySubmissions();
}
