package com.info.ocms.controller;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.UpdateCourseRequest;
import com.info.ocms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public CourseResponse createCourse(CourseRequest courseRequest)throws IOException {
       return courseService.createCourse(courseRequest);
    }
    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable Long id){
        return courseService.getCourseById(id);
    }
    @GetMapping
    public List<CourseResponse> getAllCourse(){
        return courseService.getAllCourses();
    }
    @PutMapping

    public CourseResponse updateCourse(@Valid UpdateCourseRequest updateCourseRequest)throws IOException{
        return courseService.updateCourse(updateCourseRequest);
    }
    @DeleteMapping("/{id}")
    public void deleteCourseById(@PathVariable Long id){
        courseService.deleteCourseById(id);
    }

}
