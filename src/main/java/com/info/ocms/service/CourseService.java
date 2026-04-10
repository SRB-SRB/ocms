package com.info.ocms.service;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.UpdateCourseRequest;

import java.io.IOException;
import java.util.List;

public interface CourseService {

    CourseResponse createCourse(CourseRequest courseRequest) throws IOException;
    CourseResponse getCourseById(Long id);
    List<CourseResponse> getAllCourses();
    CourseResponse updateCourse(UpdateCourseRequest updateCourseRequest)throws IOException;
    void deleteCourseById(Long id);
}

