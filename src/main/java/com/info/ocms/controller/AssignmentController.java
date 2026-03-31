package com.info.ocms.controller;

import com.info.ocms.dto.AssignmentRequest;
import com.info.ocms.dto.AssignmentResponse;
import com.info.ocms.dto.UpdateAssignmentRequest;
import com.info.ocms.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssignmentResponse createAssignment(@ModelAttribute AssignmentRequest assignmentRequest) throws IOException {
        return assignmentService.createAssignment(assignmentRequest);
    }

    @GetMapping("/{id}")
    public AssignmentResponse getAssignmentById(@PathVariable Long id){
        return assignmentService.getAssignmentById(id);
    }
    @GetMapping
    public List<AssignmentResponse> getAllAssignment(){
        return assignmentService.getAllAssignments();
    }
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssignmentResponse updateAssignment(@ModelAttribute UpdateAssignmentRequest updateAssignmentRequest)throws IOException{
        return assignmentService.updateAssignment(updateAssignmentRequest);
    }
    @DeleteMapping("/{id}")
    public void deleteAssignmentById(@PathVariable Long id){
        assignmentService.deleteAssignmentById(id);
    }


}
