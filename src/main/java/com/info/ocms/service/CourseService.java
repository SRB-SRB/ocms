package com.info.ocms.service;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;

public interface CourseService {
    CourseResponse createCourse(CourseRequest courseRequest);
}

