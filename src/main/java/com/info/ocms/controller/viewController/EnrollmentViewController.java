package com.info.ocms.controller.viewController;

import com.info.ocms.dto.EnrollmentRequest;
import com.info.ocms.dto.EnrollmentResponse;
import com.info.ocms.model.Course;
import com.info.ocms.model.User;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.EnrollmentService;
import com.info.ocms.service.serviceImpl.CoursePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enrollment/view")
@RequiredArgsConstructor
public class EnrollmentViewController {

    private final EnrollmentService enrollmentService;
    private final CoursePermissionService coursePermissionService;
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;

    // ─── Get current user from session ────────────────────────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated User Not Found"));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────
    private Course getCourse(Long id) {
        return courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course Not Found"));
    }

    // ─── Show enroll confirmation page ────────────────────────────────────────
    @GetMapping("/enroll/{courseId}")
    public String showEnrollPage(@PathVariable Long courseId, Model model) {
        Course course = getCourse(courseId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", course.getTitle());
        return "enrollment/enroll";
    }

    // ─── Enroll student in course ─────────────────────────────────────────────
    @PostMapping("/enroll")
    public String enroll(@RequestParam Long courseId) {
        User currentUser = getCurrentUser();

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setUserId(currentUser.getId());
        enrollmentRequest.setCourseId(courseId);

        enrollmentService.enroll(enrollmentRequest);
        return "redirect:/course/view/viewCourse/" + courseId;
    }

    // ─── Cancel enrollment ────────────────────────────────────────────────────
    @DeleteMapping("/cancel/{enrollmentId}")
    public String cancelEnrollment(@PathVariable Long enrollmentId,
                                   @RequestParam Long courseId) {
        // ✅ Service checks if owner before cancelling
        enrollmentService.cancelEnrollment(enrollmentId);
        return "redirect:/course/view/viewCourse/" + courseId;
    }

    // ─── View all enrollments for a course (instructor only) ──────────────────
    @GetMapping("/course/{courseId}")
    public String getCourseEnrollments(@PathVariable Long courseId, Model model) {
        User currentUser = getCurrentUser();
        Course course = getCourse(courseId);

        // ✅ Only course instructor can see all enrollments
        if (!coursePermissionService.canManageContent(currentUser, course)) {
            throw new AccessDeniedException("Only course instructors can view enrollments");
        }

        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("courseId", courseId);
        model.addAttribute("isOwner", coursePermissionService.isCourseOwner(currentUser, course));
        return "enrollment/courseEnrollments";
    }

    // ─── Promote student to course instructor ─────────────────────────────────
    @PostMapping("/promote")
    public String promoteToInstructor(@RequestParam Long userId,
                                      @RequestParam Long courseId) {
        User currentUser = getCurrentUser();
        Course course = getCourse(courseId);

        // ✅ Only course owner can promote
        if (!coursePermissionService.canPromote(currentUser, course)) {
            throw new AccessDeniedException("Only the course owner can promote students");
        }

        enrollmentService.promoteToInstructor(userId, courseId);
        return "redirect:/enrollment/view/course/" + courseId;
    }

    // ─── Demote course instructor back to student ──────────────────────────────
    @PostMapping("/demote")
    public String demoteToStudent(@RequestParam Long userId,
                                  @RequestParam Long courseId) {
        User currentUser = getCurrentUser();
        Course course = getCourse(courseId);

        // ✅ Only course owner can demote
        if (!coursePermissionService.canPromote(currentUser, course)) {
            throw new AccessDeniedException("Only the course owner can demote instructors");
        }

        enrollmentService.demoteToStudent(userId, courseId);
        return "redirect:/enrollment/view/course/" + courseId;
    }
}