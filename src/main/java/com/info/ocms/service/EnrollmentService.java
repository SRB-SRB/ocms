package com.info.ocms.service;

import com.info.ocms.dto.EnrollmentRequest;
import com.info.ocms.dto.EnrollmentResponse;
import com.info.ocms.model.Course;
import com.info.ocms.model.User;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enroll(EnrollmentRequest enrollmentRequest);
    EnrollmentResponse enrollAsOwner(User user, Course course);
    void cancelEnrollment(Long id);
    EnrollmentResponse promoteToInstructor(Long userId,Long courseId);
    EnrollmentResponse demoteToStudent(Long userId,Long courseId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    List<EnrollmentResponse> getEnrollmentsByUser(Long userId);
}
