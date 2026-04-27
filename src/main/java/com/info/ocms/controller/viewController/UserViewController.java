package com.info.ocms.controller.viewController;

import com.info.ocms.dto.EnrollmentResponse;
import com.info.ocms.dto.SubmittedAssignmentResponse;
import com.info.ocms.dto.UserRequest;
import com.info.ocms.dto.UserResponse;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.security.CustomUserDetails;
import com.info.ocms.service.EnrollmentService;
import com.info.ocms.service.SubmittedAssignmentService;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/view")
@RequiredArgsConstructor

public class UserViewController {
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final SubmittedAssignmentService submittedAssignmentService;


    @GetMapping("/dashboard")
    public String getDashboard(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model){
        UserResponse userResponse=userService.getById(customUserDetails.getId());

        List<EnrollmentResponse> enrollments=enrollmentService.getEnrollmentsByUser(customUserDetails.getId());

        List<EnrollmentResponse> myCoursesAsStudent=enrollments.stream()
                        .filter(e->e.getRoleInCourse().equals("STUDENT"))
                        .toList();
        List<EnrollmentResponse> myCoursesAsInstructor=enrollments.stream()
                        .filter(e->e.getRoleInCourse().equals("INSTRUCTOR"))
                        .toList();
        List<SubmittedAssignmentResponse> mySubmissions=submittedAssignmentService.getMySubmissions();
        List<SubmittedAssignmentResponse> gradedSubmissions=mySubmissions.stream()
                        .filter(s->s.getGrade()!=null)
                                .toList();
        List<SubmittedAssignmentResponse> pendingSubmissions=mySubmissions.stream()
                        .filter(s->s.getGrade()==null)
                                .toList();

        model.addAttribute("userResponse",userResponse);
        model.addAttribute("userId",customUserDetails.getId());
        model.addAttribute("myCoursesAsStudent",myCoursesAsStudent);
        model.addAttribute("myCoursesAsInstructor",myCoursesAsInstructor);
        model.addAttribute("gradedSubmissions",gradedSubmissions);
        model.addAttribute("pendingSubmissions",pendingSubmissions);
        return "user/dashboard";
    }

    @GetMapping("/update")
    public String showUpdateForm(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model){
        UserResponse userResponse=userService.getById(customUserDetails.getId());
        UserRequest userRequest=new UserRequest();
        userRequest.setId(userResponse.getId());
        userRequest.setName(userResponse.getName());
        userRequest.setContact(userResponse.getContact());
        userRequest.setEmail(userResponse.getEmail());
        model.addAttribute("userRequest",userRequest);
        model.addAttribute("userId",customUserDetails.getId());
        return "user/updateUserForm";
    }

    @PutMapping("/update")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,@ModelAttribute("userRequest") UserRequest userRequest){
        userService.updateUser(userRequest);
        return "redirect:/user/view/dashboard";
    }
}
