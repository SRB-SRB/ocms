package com.info.ocms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long courseId;
    private String courseTitle;
    private String roleInCourse;
}
