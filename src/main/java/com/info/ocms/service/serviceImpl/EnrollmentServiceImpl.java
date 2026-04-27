package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.RoleInCourse;
import com.info.ocms.dto.EnrollmentRequest;
import com.info.ocms.dto.EnrollmentResponse;
import com.info.ocms.model.Course;
import com.info.ocms.model.Enrollment;
import com.info.ocms.model.User;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.ropository.EnrollmentRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepo enrollmentRepo;
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;

    @Override
    public EnrollmentResponse enroll(EnrollmentRequest enrollmentRequest) {
        User user=userRepo.findById(enrollmentRequest.getUserId()).orElseThrow(()->new RuntimeException("User Not Found"));
        Course course=courseRepo.findById(enrollmentRequest.getCourseId()).orElseThrow(()->new RuntimeException("Course Not Found"));

        if(enrollmentRepo.findByUserAndCourse(user,course).isPresent()){
            throw new RuntimeException("User is Already Enrolled In this Course");
        }

        Enrollment enrollment=new Enrollment();
        enrollment.setCourse(course);
        enrollment.setUser(user);
        enrollment.setRoleInCourse(RoleInCourse.STUDENT);
        enrollment.setOwner(false);
        return mapToEnrollmentResponse(enrollmentRepo.save(enrollment));

    }

    @Override
    public EnrollmentResponse enrollAsOwner(User user, Course course) {
        Enrollment enrollment=new Enrollment();
        enrollment.setCourse(course);
        enrollment.setUser(user);
        enrollment.setRoleInCourse(RoleInCourse.INSTRUCTOR);
        enrollment.setOwner(true);
        return mapToEnrollmentResponse(enrollmentRepo.save(enrollment));
    }

    @Override
    public void cancelEnrollment(Long id) {
        Enrollment enrollment=enrollmentRepo.findById(id).orElseThrow(()->new RuntimeException("Enrollment not found"));
        if(enrollment.isOwner()){
            throw new RuntimeException("Cannot Remove the Course Owners Enrollment");
        }
        enrollmentRepo.delete(enrollment);
    }

    @Override
    public EnrollmentResponse promoteToInstructor(Long userId, Long courseId) {
        User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        Course course=courseRepo.findById(courseId).orElseThrow(()-> new RuntimeException("Course Not Found"));
        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(user,course).orElseThrow(()->new RuntimeException("User is not enrolled  in this course"));
        if(enrollment.getRoleInCourse()==RoleInCourse.INSTRUCTOR){
            throw new RuntimeException("User is Already Instructor in this course");
        }
        enrollment.setRoleInCourse(RoleInCourse.INSTRUCTOR);
        enrollment.setOwner(false);
        return mapToEnrollmentResponse(enrollmentRepo.save(enrollment));
    }

    @Override
    public EnrollmentResponse demoteToStudent(Long userId, Long courseId) {
        User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        Course course=courseRepo.findById(courseId).orElseThrow(()-> new RuntimeException("Course Not Found"));
        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(user,course).orElseThrow(()->new RuntimeException("User is not enrolled in this course"));
        if(enrollment.isOwner()){
            throw new RuntimeException("Can't demote Course Owner");
        }
        enrollment.setRoleInCourse(RoleInCourse.STUDENT);
        return mapToEnrollmentResponse(enrollmentRepo.save(enrollment));
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        Course course=courseRepo.findById(courseId).orElseThrow(()->new RuntimeException("Course Not Found"));
        return enrollmentRepo.findByCourse(course)
                .stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByUser(Long userId) {
        User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        return  enrollmentRepo.findByUser(user)
                .stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    public EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment){
        EnrollmentResponse enrollmentResponse=new EnrollmentResponse();
        enrollmentResponse.setId(enrollment.getId());
        enrollmentResponse.setCourseId(enrollment.getCourse().getId());
        enrollmentResponse.setUserId(enrollment.getUser().getId());
        enrollmentResponse.setUserName(enrollment.getUser().getName());
        enrollmentResponse.setUserEmail(enrollment.getUser().getEmail());
        enrollmentResponse.setCourseTitle(enrollment.getCourse().getTitle());
        enrollmentResponse.setRoleInCourse(enrollment.getRoleInCourse().toString());
        return enrollmentResponse;
    }


}
