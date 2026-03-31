package com.info.ocms.service;

import com.info.ocms.dto.AssignmentRequest;
import com.info.ocms.dto.AssignmentResponse;
import com.info.ocms.dto.UpdateAssignmentRequest;
import com.info.ocms.dto.UpdateCourseRequest;

import java.io.IOException;
import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest assignmentRequest) throws IOException;
    AssignmentResponse getAssignmentById(Long id);
    List<AssignmentResponse> getAllAssignments();
    AssignmentResponse updateAssignment(UpdateAssignmentRequest updateAssignmentRequest) throws IOException;
    void deleteAssignmentById(Long id);

}
