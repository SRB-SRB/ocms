package com.info.ocms.service.serviceImpl;

import com.info.ocms.dto.CourseRequest;
import com.info.ocms.dto.CourseResponse;
import com.info.ocms.dto.FileResponse;
import com.info.ocms.dto.UpdateCourseRequest;
import com.info.ocms.model.Course;
import com.info.ocms.model.CourseFile;
import com.info.ocms.ropository.CourseFileRepo;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.service.CourseService;
import com.info.ocms.service.DocumentMasterService;
import lombok.RequiredArgsConstructor;
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


    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) throws IOException {
       Course course= mapToCourse(courseRequest);
       Course savedCourse=courseRepo.save(course);
       if(courseRequest.getCourseFiles()!=null && !courseRequest.getCourseFiles().isEmpty()){
           course.setCourseFiles(saveCourseFiles(courseRequest.getCourseFiles(),savedCourse));
       }
       return mapToCourseResponse(savedCourse);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        return mapToCourseResponse(courseRepo.findById(id).orElseThrow(()-> new RuntimeException("Course Not Found")));
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<CourseResponse> courseResponses=new ArrayList<>();
        for(Course course:courseRepo.findAll()){
            courseResponses.add(mapToCourseResponseAll(course));
        }
        return courseResponses;
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(UpdateCourseRequest updateCourseRequest)throws IOException{
        Course existingCourse=courseRepo.findById(updateCourseRequest.getId()).orElseThrow(()->new RuntimeException("Course Not Found"));
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

        return mapToCourseResponse(updated);




    }

    @Override
    @Transactional
    public void deleteCourseById(Long id) {
        Course course=courseRepo.findById(id).orElseThrow(()->new RuntimeException("Course Not Found"));
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
    private CourseResponse mapToCourseResponse(Course course){
        CourseResponse courseResponse=new CourseResponse();
        courseResponse.setId(course.getId());
        courseResponse.setTitle(course.getTitle());
        courseResponse.setDescription(course.getDescription());
        List<FileResponse> fileResponses=new ArrayList<>();
        for(CourseFile courseFile:course.getCourseFiles()){
            FileResponse fileResponse=new FileResponse();
            fileResponse.setId(courseFile.getId());
            fileResponse.setDocumentGuide(courseFile.getDocumentGuid());
            fileResponses.add(fileResponse);

        }
        courseResponse.setCourseFiles(fileResponses);
        return courseResponse;
    }
    private CourseResponse mapToCourseResponseAll(Course course){
        CourseResponse courseResponse=new CourseResponse();
        courseResponse.setId(course.getId());
        courseResponse.setTitle(course.getTitle());
        courseResponse.setDescription(course.getDescription());
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
