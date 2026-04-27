package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.RoleInCourse;
import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.FileResponse;
import com.info.ocms.dto.UpdateCourseRequest;
import com.info.ocms.model.Course;
import com.info.ocms.model.CourseFile;
import com.info.ocms.model.Enrollment;
import com.info.ocms.model.User;
import com.info.ocms.ropository.CourseFileRepo;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.ropository.EnrollmentRepo;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.CourseService;
import com.info.ocms.service.DocumentMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CourseServiceImpl implements CourseService {
    private final CourseRepo courseRepo;
    private final DocumentMasterService documentMasterService;
    private final CourseFileRepo courseFileRepo;
    private final UserRepo userRepo;
    private final EnrollmentRepo enrollmentRepo;

    private User getCurrentUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("Authenticated User Not Found"));
    }



    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest courseRequest) throws IOException {
        User creator=getCurrentUser();
       Course course= mapToCourse(courseRequest);
       Course savedCourse=courseRepo.save(course);

        Enrollment enrollment=new Enrollment();

        enrollment.setUser(creator);
        enrollment.setCourse(savedCourse);
        enrollment.setRoleInCourse(RoleInCourse.INSTRUCTOR);
        enrollment.setOwner(true);
        enrollmentRepo.save(enrollment);


       if(courseRequest.getCourseFiles()!=null && !courseRequest.getCourseFiles().isEmpty()){
           course.setCourseFiles(saveCourseFiles(courseRequest.getCourseFiles(),savedCourse));
       }
       return mapToCourseResponse(savedCourse, true);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        return mapToCourseResponse(courseRepo.findById(id).orElseThrow(()-> new RuntimeException("Course Not Found")),true);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<CourseResponse> courseResponses=new ArrayList<>();
        for(Course course:courseRepo.findAll()){
            courseResponses.add(mapToCourseResponse(course,false));
        }
        return courseResponses;
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(UpdateCourseRequest updateCourseRequest)throws IOException{
        User currentUser=getCurrentUser();

        Course existingCourse=courseRepo.findById(updateCourseRequest.getId()).orElseThrow(()->new RuntimeException("Course Not Found"));


        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(currentUser,existingCourse).orElseThrow(()->new AccessDeniedException("you are not enrolled in this course"));
        if(enrollment.getRoleInCourse()!=RoleInCourse.INSTRUCTOR){
            throw new AccessDeniedException("Only Course Instructor can UPDATE the Course");
        }
        existingCourse.setTitle(updateCourseRequest.getTitle());
        existingCourse.setDescription(updateCourseRequest.getDescription());
        var existingFiles=existingCourse.getCourseFiles();


        for(var existingFile:existingFiles){
            if(!updateCourseRequest.getKeepFilesIds().contains(existingFile.getId())){
                documentMasterService.deleteByDocumentGuide(existingFile.getDocumentGuid());
                courseFileRepo.deleteById(existingFile.getId());

            }
        }
        Iterator<CourseFile> iterator=existingFiles.iterator();
        while (iterator.hasNext()){
            CourseFile file=iterator.next();
            if (!updateCourseRequest.getKeepFilesIds().contains(file.getId())) {
                iterator.remove();
            }
        }

        if(updateCourseRequest.getCourseFiles()!=null && !updateCourseRequest.getCourseFiles().isEmpty()){
            List<CourseFile> newFiles=saveCourseFiles(updateCourseRequest.getCourseFiles(),existingCourse);
            existingFiles.addAll(newFiles);
        }

        Course updated=courseRepo.save(existingCourse);

        return mapToCourseResponse(updated,true);
    }


    @Override
    @Transactional
    public void deleteCourseById(Long id) {
        User currentUser=getCurrentUser();
        Course course=courseRepo.findById(id).orElseThrow(()->new RuntimeException("Course Not Found"));
        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(currentUser,course).orElseThrow(()->new AccessDeniedException("You are not enrolled in this course"));
        if(!enrollment.isOwner()){
            throw new AccessDeniedException("Only the course owner can delete this course");
        }
        for (CourseFile courseFile:course.getCourseFiles()){
           documentMasterService.deleteByDocumentGuide(courseFile.getDocumentGuid());
        }
        courseRepo.deleteById(id);
    }


    private Course mapToCourse(CourseRequest courseRequest){
        Course course =new Course();
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        return course;

    }
    private CourseResponse mapToCourseResponse(Course course, boolean includeFiles){
        CourseResponse courseResponse=new CourseResponse();
        courseResponse.setId(course.getId());
        courseResponse.setTitle(course.getTitle());
        courseResponse.setDescription(course.getDescription());
        if(includeFiles && course.getCourseFiles()!=null){
            List<FileResponse> fileResponses=new ArrayList<>();
            for(CourseFile courseFile:course.getCourseFiles()){
                FileResponse fileResponse=new FileResponse();
                fileResponse.setId(courseFile.getId());
                fileResponse.setDocumentGuide(courseFile.getDocumentGuid());
                fileResponses.add(fileResponse);
            }
            courseResponse.setCourseFiles(fileResponses);
        }
        return courseResponse;
    }


    private List<CourseFile> saveCourseFiles(List<MultipartFile> files, Course course)throws IOException{
        List<CourseFile> courseFiles=new ArrayList<>();
        for(MultipartFile file: files){
            if(file!=null && !file.isEmpty()) {
                CourseFile courseFile = new CourseFile();
                courseFile.setDocumentGuid(documentMasterService.createFile(file, "course_file").getDocumentGuid());
                courseFile.setCourse(course);
                courseFiles.add(courseFileRepo.save(courseFile));
            }
        }
        return courseFiles;

    }

}
