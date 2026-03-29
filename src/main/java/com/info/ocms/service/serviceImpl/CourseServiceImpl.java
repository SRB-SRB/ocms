package com.info.ocms.service.serviceImpl;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.model.Course;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepo courseRepo;

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
       Course course= mapToCourse(courseRequest);
       courseRequest.getCourseFiles();


        return null;
    }


    private Course mapToCourse(CourseRequest courseRequest){
        Course course =new Course();
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        return course;

    }
}
