package com.info.ocms.controller.viewController;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.DocumentMasterResponse;
import com.info.ocms.dto.UpdateCourseRequest;
import com.info.ocms.model.Course;
import com.info.ocms.model.User;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.ropository.EnrollmentRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.CourseService;
import com.info.ocms.service.DocumentMasterService;
import com.info.ocms.service.serviceImpl.CoursePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/course/view")
@RequiredArgsConstructor
public class CourseViewController {
    private final CourseService courseService;
    private final CoursePermissionService coursePermissionService;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final DocumentMasterService documentMasterService;

    private User getCurrentUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
       return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("Authenticated User Not Found"));
    }
    private Course getCourse(Long id){
        return courseRepo.findById(id).orElseThrow(()->new RuntimeException("Course Not Found"));
    }


    @GetMapping("/courses")
    public String showCourses(Model model){
        List<CourseResponse> courseResponses=courseService.getAllCourses();
        model.addAttribute("courseResponses",courseResponses);
        return "course/courses";
    }
    @GetMapping("/createCourse")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public String showCourseForm(Model model){
        model.addAttribute("courseRequest",new CourseRequest());
        return "course/createCourse";
    }
    @PostMapping("/createCourse")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public String createCourse(@ModelAttribute CourseRequest courseRequest)throws IOException {
        courseService.createCourse(courseRequest);
        return "redirect:/course/view/courses";

    }
    @GetMapping("/viewCourse/{id}")
    public String getCourse(@PathVariable Long id,Model model){
        User currentUser=getCurrentUser();
        Course course= getCourse(id);
        CourseResponse courseResponse=courseService.getCourseById(id);
        boolean isEnrolled=enrollmentRepo.findByUserAndCourse(currentUser,course).isPresent();
        model.addAttribute("courseResponse",courseResponse);
        model.addAttribute("courseId",id);
        model.addAttribute("canManage",coursePermissionService.canManageContent(currentUser,course));
        model.addAttribute("isOwner",coursePermissionService.isCourseOwner(currentUser,course));
        model.addAttribute("isEnrolled",isEnrolled);
        return"course/viewCourse";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id,Model model){
        User currentUser=getCurrentUser();
        Course course=getCourse(id);
        if(!coursePermissionService.canManageContent(currentUser,course)){
            throw new AccessDeniedException("Only Course Instructor Can Update");
        }
        CourseResponse courseResponse=courseService.getCourseById(id);
        UpdateCourseRequest updateCourseRequest=new UpdateCourseRequest();
        updateCourseRequest.setId(id);
        updateCourseRequest.setTitle(courseResponse.getTitle());
        updateCourseRequest.setDescription(courseResponse.getDescription());
        updateCourseRequest.setExistingCourseFiles(courseResponse.getCourseFiles());
        model.addAttribute("updateCourseRequest",updateCourseRequest);
        model.addAttribute("courseId",id);
        return "course/updateCourseForm";
    }
    @PutMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id,@ModelAttribute("updateCourseRequest") UpdateCourseRequest updateCourseRequest)throws IOException{
        courseService.updateCourse(updateCourseRequest);
        return"redirect:/course/view/viewCourse/"+id;
    }
    @DeleteMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id){
        courseService.deleteCourseById(id);
        return "redirect:/course/view/courses";
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

