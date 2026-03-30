package com.info.ocms.service;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest courseRequest) throws IOException;
    CourseResponse getCourseById(Long id);
    List<CourseResponse> getAllCourses();
    void deleteCourseById(Long id);
}

