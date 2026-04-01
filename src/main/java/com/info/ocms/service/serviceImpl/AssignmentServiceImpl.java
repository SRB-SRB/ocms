package com.info.ocms.service.serviceImpl;

import com.info.ocms.dto.*;
import com.info.ocms.model.Assignment;
import com.info.ocms.model.AssignmentFile;
import com.info.ocms.model.Course;
import com.info.ocms.model.CourseFile;
import com.info.ocms.ropository.AssignmentFileRepo;
import com.info.ocms.ropository.AssignmentRepo;
import com.info.ocms.ropository.CourseRepo;
import com.info.ocms.service.AssignmentService;
import com.info.ocms.service.DocumentMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepo assignmentRepo;
    private final AssignmentFileRepo assignmentFileRepo;
    private final DocumentMasterService documentMasterService;
    private final SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer;
    private final CourseRepo courseRepo;

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest assignmentRequest) throws IOException {
        Assignment assignment=mapToAssignment(assignmentRequest);
        assignment.setCourse(courseRepo.findById(assignmentRequest.getCourseId()).orElseThrow(()->new RuntimeException("Course Not Found")));
        Assignment savedAssignment=assignmentRepo.save(assignment);
        if(assignmentRequest.getAssignmentFiles()!=null && !assignmentRequest.getAssignmentFiles().isEmpty()){
            assignment.setAssignmentFiles(saveAssignmentFiles(assignmentRequest.getAssignmentFiles(),savedAssignment));
        }

        return mapToAssignmentResponse(savedAssignment);
    }

    @Override
    public AssignmentResponse getAssignmentById(Long id) {
     return mapToAssignmentResponse(assignmentRepo.findById(id).orElseThrow(()->new RuntimeException("Assignment Not Found")));
    }

    @Override
    public List<AssignmentResponse> getAllAssignments() {
        List<AssignmentResponse> assignmentResponses=new ArrayList<>();
        for(Assignment assignment:assignmentRepo.findAll()){
           assignmentResponses.add(mapToAssignmentResponse(assignment));
        }
        return assignmentResponses;
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(UpdateAssignmentRequest updateAssignmentRequest) throws IOException{
        Assignment existingAssignment=assignmentRepo.findById(updateAssignmentRequest.getId()).orElseThrow(()->new RuntimeException("Assignment Not Found"));
        existingAssignment.setTitle(updateAssignmentRequest.getTitle());
        existingAssignment.setDescription(updateAssignmentRequest.getDescription());
        existingAssignment.setDueDate(updateAssignmentRequest.getDueDate());
        var existingFiles=existingAssignment.getAssignmentFiles();

        for(var existingFile:existingFiles){
            if(!updateAssignmentRequest.getKeepFilesIds().contains(existingFile.getId())){
                documentMasterService.deleteByDocumentGuide(existingFile.getDocumentGuid());
                assignmentFileRepo.deleteById(existingFile.getId());
            }
        }
        Iterator<AssignmentFile> iterator=existingFiles.iterator();
        while(iterator.hasNext()){
            AssignmentFile file=iterator.next();
            if(!updateAssignmentRequest.getKeepFilesIds().contains(file.getId())){
                iterator.remove();
            }
        }
        if(updateAssignmentRequest.getAssignmentFiles()!=null&& !updateAssignmentRequest.getAssignmentFiles().isEmpty()){
            List<AssignmentFile> newFiles=saveAssignmentFiles(updateAssignmentRequest.getAssignmentFiles(),existingAssignment);
            existingFiles.addAll(newFiles);
        }
        Assignment updated=assignmentRepo.save(existingAssignment);
        return mapToAssignmentResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAssignmentById(Long id) {
        Assignment assignment=assignmentRepo.findById(id).orElseThrow(()-> new RuntimeException("Assignment Not Found"));
        for(AssignmentFile assignmentFile:assignment.getAssignmentFiles()){
            documentMasterService.deleteByDocumentGuide(assignmentFile.getDocumentGuid());
        }
        assignmentRepo.deleteById(id);

    }

    private Assignment mapToAssignment(AssignmentRequest assignmentRequest){
        Assignment assignment=new Assignment();
        assignment.setTitle(assignmentRequest.getTitle());
        assignment.setDescription(assignmentRequest.getDescription());
        assignment.setDueDate(assignmentRequest.getDueDate());
        return assignment;
    }

    private AssignmentResponse mapToAssignmentResponse(Assignment assignment){
        AssignmentResponse assignmentResponse=new AssignmentResponse();
        assignmentResponse.setId(assignment.getId());
        assignmentResponse.setTitle(assignment.getTitle());
        assignmentResponse.setDescription(assignment.getDescription());
        assignmentResponse.setCreationDate(assignment.getCreationDate().toString());
        assignmentResponse.setDueDate(assignment.getDueDate().toString());
        List<FileResponse> fileResponses=new ArrayList<>();
        for(AssignmentFile assignmentFile: assignment.getAssignmentFiles()){
            FileResponse fileResponse=new FileResponse();
            fileResponse.setId(assignmentFile.getId());
            fileResponse.setDocumentGuide(assignmentFile.getDocumentGuid());
            fileResponses.add(fileResponse);
        }
        assignmentResponse.setAssignmentFiles(fileResponses);
//        courseRepo.findById(assignment.getId()).orElseThrow(()->new RuntimeException("Course Not Found"))
       Course course= assignment.getCourse();
        assignmentResponse.setCourseName(course.getTitle());
        return assignmentResponse;
    }
    private List<AssignmentFile> saveAssignmentFiles(List<MultipartFile> files, Assignment assignment)throws IOException {
        List<AssignmentFile> assignmentFiles=new ArrayList<>();
        for(MultipartFile file: files){
            if(file!=null && !file.isEmpty()) {
                AssignmentFile assignmentFile = new AssignmentFile();
                assignmentFile.setDocumentGuid(documentMasterService.createFile(file, "assignment_file").getDocumentGuid());
                assignmentFile.setAssignment(assignment);
                assignmentFiles.add(assignmentFileRepo.save(assignmentFile));
            }
        }
        return assignmentFiles;

    }

}
