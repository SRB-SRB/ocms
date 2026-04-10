package com.info.ocms.controller.viewController;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.UpdateCourseRequest;
import com.info.ocms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/course/view")
@RequiredArgsConstructor
public class CourseViewController {
    private final CourseService courseService;

    @GetMapping("/courses")
    public String showCourses(Model model){
        List<CourseResponse> courseResponses=courseService.getAllCourses();
        model.addAttribute("CourseResponses",courseResponses);
        return "course/courses";
    }
    @GetMapping("/createCourse")
    public String showCourseForm(Model model){
        model.addAttribute("courseRequest",new CourseRequest());
        return "course/createCourse";
    }
    @PostMapping("/createCourse")
    public String createCourse(@ModelAttribute CourseRequest courseRequest)throws IOException {
        courseService.createCourse(courseRequest);
        return "redirect:/course/view/createCourse";

    }
    @GetMapping("/viewCourse/{id}")
    public String getCourse(@PathVariable Long id,Model model){
        CourseResponse courseResponse=courseService.getCourseById(id);
        model.addAttribute("courseResponse",courseResponse);
        model.addAttribute("courseId",id);
        return"course/viewCourse";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id,Model model){
        CourseResponse courseResponse=courseService.getCourseById(id);
        UpdateCourseRequest updateCourseRequest=new UpdateCourseRequest();
        updateCourseRequest.setId(id);
        updateCourseRequest.setTitle(courseResponse.getTitle());
        updateCourseRequest.setDescription(courseResponse.getDescription());
        updateCourseRequest.setExistingCourseFiles(courseResponse.getCourseFiles());
        model.addAttribute("updateCourseRequest",updateCourseRequest);
        model.addAttribute("courseId",id);
        return "course/updateCourseForm";
    }
    @PutMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id,@ModelAttribute("updateCourseRequest") UpdateCourseRequest updateCourseRequest)throws IOException{
        courseService.updateCourse(updateCourseRequest);
        return"redirect:/course/view/viewCourse/"+id;
    }




}

