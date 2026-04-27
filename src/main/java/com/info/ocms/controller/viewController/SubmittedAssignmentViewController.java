package com.info.ocms.controller.viewController;

import com.info.ocms.dto.InstructorSubmittedAssignmentRequest;
import com.info.ocms.dto.StudentSubmittedAssignmentRequest;
import com.info.ocms.dto.SubmittedAssignmentResponse;
import com.info.ocms.dto.UpdateStudentSubmittedAssignmentRequest;
import com.info.ocms.model.Assignment;
import com.info.ocms.model.Course;
import com.info.ocms.model.User;
import com.info.ocms.ropository.AssignmentRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.SubmittedAssignmentService;
import com.info.ocms.service.serviceImpl.CoursePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/submittedAssignment/view")
@RequiredArgsConstructor
public class SubmittedAssignmentViewController {
    private final SubmittedAssignmentService submittedAssignmentService;
    private final CoursePermissionService coursePermissionService;
    private final AssignmentRepo assignmentRepo;
    private final UserRepo userRepo;

    private User getCurrentUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("Authenticated User Not Found"));

    }
    private Assignment getAssignment(Long id){
        return assignmentRepo.findById(id).orElseThrow(()->new RuntimeException("Assignment Not Found"));
    }

    @GetMapping("/viewSubmittedAssignment/{id}")
    public String getSubmittedAssignment(@PathVariable Long id, Model model){
        User currentUser=getCurrentUser();
        SubmittedAssignmentResponse submittedAssignmentResponse=submittedAssignmentService.getSubmittedAssignmentById(id);
        Assignment assignment=getAssignment(submittedAssignmentResponse.getAssignmentId());
        Course course=assignment.getCourse();

       model.addAttribute("submittedAssignmentResponse",submittedAssignmentResponse);
       model.addAttribute("submittedAssignmentId",id);
       model.addAttribute("canManage",coursePermissionService.canManageContent(currentUser,course));
        model.addAttribute("isOwner", submittedAssignmentResponse.getUserId().equals(currentUser.getId()));
       return "submittedAssignment/viewSubmittedAssignment";
    }
    @GetMapping("/submitAssignment/{assignmentId}")
    public String showAssignmentForm(@PathVariable Long assignmentId, Model model){
       StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest= new StudentSubmittedAssignmentRequest();
       studentSubmittedAssignmentRequest.setAssignmentId(assignmentId);
        model.addAttribute("studentSubmitAssignmentRequest",studentSubmittedAssignmentRequest);
        return"submittedAssignment/submitAssignment";
    }
    @PostMapping("/submitAssignment")
    public String submitAssignment(@ModelAttribute StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest) throws IOException {
        submittedAssignmentService.submitAssignment(studentSubmittedAssignmentRequest);
        return "redirect:/assignment/view/viewAssignment/"+studentSubmittedAssignmentRequest.getAssignmentId();
    }

    @GetMapping("submissions/{assignmentId}")
    public String getSubmissions(@PathVariable Long assignmentId,Model model){
        List<SubmittedAssignmentResponse> submissions=submittedAssignmentService.getSubmissionsByAssignment(assignmentId);
        model.addAttribute("submissions",submissions);
        model.addAttribute("assignmentId",assignmentId);
        return "submittedAssignment/submissions";
    }


    @GetMapping("/gradeSubmittedAssignment/{id}")
    public String showGradeForm(@PathVariable Long id,Model model){
        User currentUser=getCurrentUser();
        SubmittedAssignmentResponse submittedAssignmentResponse=submittedAssignmentService.getSubmittedAssignmentById(id);
        Assignment assignment=getAssignment(submittedAssignmentResponse.getAssignmentId());
        if(!coursePermissionService.canManageContent(currentUser,assignment.getCourse())){
            throw new AccessDeniedException("Only Course Instructor can grade Assignment");
        }
        InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest=new InstructorSubmittedAssignmentRequest();
        instructorSubmittedAssignmentRequest.setId(id);
        model.addAttribute("instructorSubmittedAssignmentRequest",instructorSubmittedAssignmentRequest);
        return"submittedAssignment/gradeAssignment";
    }
    @PostMapping("/gradeSubmittedAssignment")
    public String gradeAssignment(@ModelAttribute("instructorSubmittedAssignmentRequest") InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest){
        submittedAssignmentService.gradeAssignment(instructorSubmittedAssignmentRequest);
        return "redirect:/submittedAssignment/view/viewSubmittedAssignment/"+instructorSubmittedAssignmentRequest.getId();
    }
    @GetMapping("/updateSubmittedAssignment/{id}")
    public String showUpdateForm(@PathVariable Long id,Model model){
        User currentUser=getCurrentUser();
        SubmittedAssignmentResponse submittedAssignmentResponse=submittedAssignmentService.getSubmittedAssignmentById(id);
        if(!submittedAssignmentResponse.getUserId().equals(currentUser.getId())){
            throw new AccessDeniedException("you can only update your own submission");
        }
        UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest=new UpdateStudentSubmittedAssignmentRequest();
        updateStudentSubmittedAssignmentRequest.setId(id);
        updateStudentSubmittedAssignmentRequest.setExistingFiles(submittedAssignmentResponse.getSubmittedAssignmentFiles());
        model.addAttribute("updateStudentSubmittedAssignmentRequest",updateStudentSubmittedAssignmentRequest);
        model.addAttribute("submittedId",id);
        return"submittedAssignment/updateSubmittedAssignment";
    }
    @PutMapping("/updateSubmittedAssignment")
    public String updateSubmittedAssignment(@ModelAttribute UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest)throws IOException{
        submittedAssignmentService.updateSubmittedAssignment(updateStudentSubmittedAssignmentRequest);
        return"redirect:/submittedAssignment/view/viewSubmittedAssignment/"+updateStudentSubmittedAssignmentRequest.getId();
    }



}
