package com.info.ocms.controller.viewController;

import com.info.ocms.dto.SubmittedAssignmentResponse;
import com.info.ocms.service.SubmittedAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/submittedAssignment/view")
@RequiredArgsConstructor
public class SubmittedAssignmentViewController {
    private final SubmittedAssignmentService submittedAssignmentService;

    @GetMapping("/viewSubmittedAssignment/{id}")
    public String getSubmittedAssignment(@PathVariable Long id, Model model){
       SubmittedAssignmentResponse submittedAssignmentResponse= submittedAssignmentService.getSubmittedAssignmentById(id);
       model.addAttribute("submittedAssignmentResponse",submittedAssignmentResponse);
       model.addAttribute("submittedAssignmentId",id);
       return "submittedAssignment/viewSubmittedAssignment";
    }


}
