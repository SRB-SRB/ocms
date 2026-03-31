package com.info.ocms.controller;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.UpdateCourseRequest;
import com.info.ocms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public CourseResponse createCourse(@ModelAttribute CourseRequest courseRequest)throws IOException {
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

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CourseResponse updateCourse(@Valid @ModelAttribute UpdateCourseRequest updateCourseRequest)throws IOException{
        return courseService.updateCourse(updateCourseRequest);
    }
    @DeleteMapping("/{id}")
    public void deleteCourseById(@PathVariable Long id){
        courseService.deleteCourseById(id);
    }

}
