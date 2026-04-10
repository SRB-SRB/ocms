package com.info.ocms.service;

import com.info.ocms.dto.EnrollmentRequest;
import com.info.ocms.dto.EnrollmentResponse;

public interface EnrollmentService {
    EnrollmentResponse enroll(EnrollmentRequest enrollmentRequest);
    void cancelEnrollment(Long id);
}
