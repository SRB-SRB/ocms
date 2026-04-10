package com.info.ocms.controller.viewController;

import com.info.ocms.dto.AssignmentRequest;
import com.info.ocms.dto.AssignmentResponse;
import com.info.ocms.dto.UpdateAssignmentRequest;
import com.info.ocms.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/assignment/view")
@RequiredArgsConstructor
public class AssignmentViewController {
    private final AssignmentService assignmentService;

    @GetMapping("/assignments")
    public String showAssignments(Model model){
        List<AssignmentResponse> assignmentResponses=assignmentService.getAllAssignments();
        model.addAttribute("assignmentResponses",assignmentResponses);
        return"assignment/assignments";
    }


    @GetMapping("/createAssignment/{id}")
    public String showAssignmentForm(@PathVariable String id,Model model){
        AssignmentRequest assignmentRequest=new AssignmentRequest();
        assignmentRequest.setCourseId(Long.parseLong(id));
        model.addAttribute("assignmentRequest",assignmentRequest);
        return "assignment/createAssignment";
    }
    @PostMapping("/createAssignment")
    public String createAssignment(@ModelAttribute AssignmentRequest assignmentRequest) throws IOException {
        assignmentService.createAssignment(assignmentRequest);
        return"redirect:/course/view/viewCourse/"+assignmentRequest.getCourseId();

    }
    @GetMapping("/viewAssignment/{id}")
    public String getAssignment(@PathVariable Long id,Model model){
        AssignmentResponse assignmentResponse=assignmentService.getAssignmentById(id);
        model.addAttribute("assignmentResponse",assignmentResponse);
        model.addAttribute("assignmentId",id);
        return "assignment/viewAssignment";
    }


    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id,Model model){
        AssignmentResponse assignmentResponse=assignmentService.getAssignmentById(id);
        UpdateAssignmentRequest updateAssignmentRequest=new UpdateAssignmentRequest();
        updateAssignmentRequest.setId(id);
        updateAssignmentRequest.setTitle(assignmentResponse.getTitle());
        updateAssignmentRequest.setDescription(assignmentResponse.getDescription());
        updateAssignmentRequest.setDueDate(LocalDate.parse(assignmentResponse.getDueDate()));
        updateAssignmentRequest.setExistingFiles(assignmentResponse.getAssignmentFiles());
        model.addAttribute("updateAssignmentRequest",updateAssignmentRequest);
        model.addAttribute("assignmentId",id);
        return "assignment/updateAssignmentForm";
    }
    @PutMapping("/update/{id}")
    public String updateAssignment(@PathVariable Long id,@ModelAttribute UpdateAssignmentRequest updateAssignmentRequest)throws IOException{
        assignmentService.updateAssignment(updateAssignmentRequest);
        return"redirect:/assignment/view/viewAssignment/"+id;
    }


}
