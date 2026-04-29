package com.info.ocms.controller.viewController;

import com.info.ocms.dto.AssignmentRequest;
import com.info.ocms.dto.AssignmentResponse;
import com.info.ocms.dto.DocumentMasterResponse;
import com.info.ocms.dto.UpdateAssignmentRequest;
import com.info.ocms.model.Course;
import com.info.ocms.model.User;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.ropository.EnrollmentRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.AssignmentService;
import com.info.ocms.service.DocumentMasterService;
import com.info.ocms.service.serviceImpl.CoursePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/assignment/view")
@RequiredArgsConstructor
public class AssignmentViewController {
    private final AssignmentService assignmentService;
    private final CoursePermissionService coursePermissionService;
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final DocumentMasterService documentMasterService;

    private User getCurrenUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
      return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("Authenticated User Not Found"));
    }


    @GetMapping("/assignments/{courseId}")
    public String showAssignments(@PathVariable Long courseId, Model model){
        User currentUser=getCurrenUser();
        Course course=courseRepo.findById(courseId).orElseThrow(()->new RuntimeException("Course Not Found"));
        List<AssignmentResponse> assignmentResponses=assignmentService.getAssignmentsByCourse(courseId);
        model.addAttribute("assignmentResponses",assignmentResponses);
        model.addAttribute("courseId",courseId);
        model.addAttribute("canManage",coursePermissionService.canManageContent(currentUser,course));
        return"assignment/assignments";
    }


    @GetMapping("/createAssignment/{courseId}")
    public String showAssignmentForm(@PathVariable Long courseId,Model model){
        User currentUser=getCurrenUser();
        Course course=courseRepo.findById(courseId).orElseThrow(()->new RuntimeException("Course not Found"));
        if(!coursePermissionService.canManageContent(currentUser,course)){
            throw new AccessDeniedException("Only Course Instructor can Create Assignment");
        }
        AssignmentRequest assignmentRequest=new AssignmentRequest();
        assignmentRequest.setCourseId(courseId);
        model.addAttribute("assignmentRequest",assignmentRequest);
        return "assignment/createAssignment";
    }

    @PostMapping("/createAssignment")
    public String createAssignment(@ModelAttribute AssignmentRequest assignmentRequest) throws IOException {
        assignmentService.createAssignment(assignmentRequest);
        return"redirect:/assignment/view/assignments/"+assignmentRequest.getCourseId();

    }
    @GetMapping("/viewAssignment/{id}")
    public String getAssignment(@PathVariable Long id,Model model){
        User currentUser=getCurrenUser();
        AssignmentResponse assignmentResponse=assignmentService.getAssignmentById(id);
        Course course=courseRepo.findById(assignmentResponse.getCourseId()).orElseThrow(()->new RuntimeException("Course Not Found"));
        boolean isEnrolled=enrollmentRepo.findByUserAndCourse(currentUser,course).isPresent();

        model.addAttribute("assignmentResponse",assignmentResponse);
        model.addAttribute("assignmentId",id);
        model.addAttribute("canManage",coursePermissionService.canManageContent(currentUser,course));
        model.addAttribute("isOwner",coursePermissionService.isCourseOwner(currentUser,course));
        model.addAttribute("isEnrolled",isEnrolled);
        model.addAttribute("submittedAssignment",coursePermissionService.submittedAssignment(currentUser.getId(), id));
        return "assignment/viewAssignment";
    }


    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id,Model model){
        User currentUser=getCurrenUser();
        AssignmentResponse assignmentResponse=assignmentService.getAssignmentById(id);
        Course course=courseRepo.findById(assignmentResponse.getCourseId()).orElseThrow(()->new RuntimeException("Course Not Found"));
        if(!coursePermissionService.canManageContent(currentUser,course)){
            throw new AccessDeniedException("Only Course Instructor can edit Assignment");
        }
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
    @DeleteMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable Long id){
        AssignmentResponse assignmentResponse=assignmentService.getAssignmentById(id);
        Long courseid=assignmentResponse.getCourseId();
        assignmentService.deleteAssignmentById(id);
        return "redirect:/assignment/view/assignments/"+courseid;
    }

    @GetMapping("/file/download/{documentGuide}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String documentGuide) throws IOException{
        DocumentMasterResponse documentMasterResponse=documentMasterService.getByDocumentGuide(documentGuide);
        Path filePath= Paths.get(documentMasterResponse.getFilePath());
        Resource resource=new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+documentMasterResponse.getFileName()+"\"")
                .contentType(MediaType.parseMediaType(documentMasterResponse.getMimeType()))
                .body(resource);

    }


}
