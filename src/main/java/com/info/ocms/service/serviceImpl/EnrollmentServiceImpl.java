package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.RoleInCourse;
import com.info.ocms.dto.EnrollmentRequest;
import com.info.ocms.dto.EnrollmentResponse;
import com.info.ocms.model.Enrollment;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.ropository.EnrollmentRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.CourseService;
import com.info.ocms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepo enrollmentRepo;
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;

    @Override
    public EnrollmentResponse enroll(EnrollmentRequest enrollmentRequest) {
        return mapToEnrollmentResponse(enrollmentRepo.save(mapToEnrollment(enrollmentRequest)));
    }

    @Override
    public void cancelEnrollment(Long id) {
        enrollmentRepo.deleteById(id);
    }

    public Enrollment mapToEnrollment(EnrollmentRequest enrollmentRequest){
        Enrollment enrollment=new Enrollment();
        enrollment.setCourse(courseRepo.findById(enrollmentRequest.getCourseId()).orElseThrow(()->new RuntimeException("Course Not Found")));
        enrollment.setUser(userRepo.findById(enrollmentRequest.getUserId()).orElseThrow(()->new RuntimeException("User Not Found")));
        enrollment.setRoleInCourse(RoleInCourse.valueOf(enrollmentRequest.getRoleInCourse()));
        return enrollment;
    }
    public EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment){
        EnrollmentResponse enrollmentResponse=new EnrollmentResponse();
        enrollmentResponse.setId(enrollment.getId());
        enrollmentResponse.setCourseId(enrollment.getCourse().getId());
        enrollmentResponse.setUserId(enrollment.getUser().getId());
        enrollmentResponse.setRoleInCourse(enrollment.getRoleInCourse().toString());
        return enrollmentResponse;
    }


}
