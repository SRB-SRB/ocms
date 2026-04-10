package com.info.ocms.controller;

import com.info.ocms.dto.InstructorSubmittedAssignmentRequest;
import com.info.ocms.dto.StudentSubmittedAssignmentRequest;
import com.info.ocms.dto.SubmittedAssignmentResponse;
import com.info.ocms.dto.UpdateStudentSubmittedAssignmentRequest;
import com.info.ocms.model.SubmittedAssignment;
import com.info.ocms.service.SubmittedAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/submittedAssignment")
@RequiredArgsConstructor
public class SubmittedAssignmentController {
    private final SubmittedAssignmentService submittedAssignmentService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SubmittedAssignmentResponse submitAssignment(@ModelAttribute StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest)throws IOException {
        return submittedAssignmentService.submitAssignment(studentSubmittedAssignmentRequest);

    }
    @PatchMapping
    public SubmittedAssignmentResponse gradeAssignment(@RequestBody InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest){
        return submittedAssignmentService.gradeAssignment(instructorSubmittedAssignmentRequest);
    }
    @GetMapping("/{id}")
    public SubmittedAssignmentResponse getSubmittedAssignmentById(@PathVariable Long id){
        return submittedAssignmentService.getSubmittedAssignmentById(id);
    }


   @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SubmittedAssignmentResponse updateSubmittedAssignment(@ModelAttribute UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest) throws IOException{
        return submittedAssignmentService.updateSubmittedAssignment(updateStudentSubmittedAssignmentRequest);
   }
}
