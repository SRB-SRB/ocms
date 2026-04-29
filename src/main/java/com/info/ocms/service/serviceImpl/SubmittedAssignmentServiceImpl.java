package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.RoleInCourse;
import com.info.ocms.dto.*;
import com.info.ocms.model.*;
import com.info.ocms.ropository.*;
import com.info.ocms.service.DocumentMasterService;
import com.info.ocms.service.SubmittedAssignmentService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmittedAssignmentServiceImpl implements SubmittedAssignmentService {
    private final AssignmentRepo assignmentRepo;
    private final SubmittedAssignmentFileRepo submittedAssignmentFileRepo;
    private final DocumentMasterService documentMasterService;
    private final SubmittedAssignmentRepo submittedAssignmentRepo;
    private final UserRepo userRepo;
    private final EnrollmentRepo enrollmentRepo;

    private User getCurrentUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepo.findByEmail(email).orElseThrow(()-> new RuntimeException("Authenticated User Not Found"));
    }

    @Override
    @Transactional
    public SubmittedAssignmentResponse submitAssignment(StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest) throws IOException {
        User currentUser=getCurrentUser();
        Assignment assignment=assignmentRepo.findById(studentSubmittedAssignmentRequest.getAssignmentId()).orElseThrow(()-> new RuntimeException("Assignment Not Found"));
        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(currentUser,assignment.getCourse())
                .orElseThrow(()->new AccessDeniedException("You are not enrolled in this course"));
        if(enrollment.getRoleInCourse()== RoleInCourse.INSTRUCTOR){
            throw new AccessDeniedException("Instructor cannot Submit Assignment");
        }

        if(submittedAssignmentRepo.findByUserIdAndAssignment(currentUser.getId(),assignment).isPresent()){
            throw new RuntimeException("You have already submitted this assignment");
        }

        SubmittedAssignment submittedAssignment=new SubmittedAssignment();
        submittedAssignment.setUserId(currentUser.getId());
        submittedAssignment.setAssignment(assignment);
        SubmittedAssignment savedSubmittedAssignment=submittedAssignmentRepo.save(submittedAssignment);
        if (studentSubmittedAssignmentRequest.getSubmittedAssignmentFiles()!=null && !studentSubmittedAssignmentRequest.getSubmittedAssignmentFiles().isEmpty()){
            savedSubmittedAssignment.setSubmittedAssignmentFiles(saveSubmittedAssignmentFiles(studentSubmittedAssignmentRequest.getSubmittedAssignmentFiles(),savedSubmittedAssignment));
        }

        return mapToSubmittedAssignmentResponse(savedSubmittedAssignment);
    }

    @Override
    public SubmittedAssignmentResponse gradeAssignment(InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest) {
        User currentUser=getCurrentUser();
        SubmittedAssignment submittedAssignment=submittedAssignmentRepo.findById(instructorSubmittedAssignmentRequest.getId()).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found"));

        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(currentUser,submittedAssignment.getAssignment().getCourse()).orElseThrow(()->new AccessDeniedException("You are not enrolled in this course"));
        if(enrollment.getRoleInCourse()!=RoleInCourse.INSTRUCTOR){
            throw  new AccessDeniedException("Only Course Instructor can grade Assignments");
        }
        submittedAssignment.setGrade(instructorSubmittedAssignmentRequest.getGrade());
        submittedAssignment.setFeedBack(instructorSubmittedAssignmentRequest.getFeedBack());

        return mapToSubmittedAssignmentResponse(submittedAssignmentRepo.save(submittedAssignment));
    }

    @Override
    public SubmittedAssignmentResponse getSubmittedAssignmentById(Long id) {
        return mapToSubmittedAssignmentResponse(submittedAssignmentRepo.findById(id).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found")));
    }

    @Override
    public List<SubmittedAssignmentResponse> getSubmissionsByAssignment(Long assignmentId) {
        Assignment assignment=assignmentRepo.findById(assignmentId).orElseThrow(()->new RuntimeException("Assignment Not Found"));
        User currentUser=getCurrentUser();
        Enrollment enrollment=enrollmentRepo.findByUserAndCourse(currentUser,assignment.getCourse()).orElseThrow(()->new AccessDeniedException("you are not enrolled in this course"));
        if(enrollment.getRoleInCourse()!=RoleInCourse.INSTRUCTOR){
            throw new AccessDeniedException("Only Course Instructor can View All Submissions");
        }
        List<SubmittedAssignmentResponse> responses=new ArrayList<>();
        for(SubmittedAssignment submission :submittedAssignmentRepo.findByAssignment(assignment)){
            responses.add(mapToSubmittedAssignmentResponse(submission));
        }
        return responses;
    }

    @Override
    @Transactional
    public SubmittedAssignmentResponse updateSubmittedAssignment(UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest) throws  IOException {
        User currentUser=getCurrentUser();
        SubmittedAssignment existingSubmittedAssignment=submittedAssignmentRepo.findById(updateStudentSubmittedAssignmentRequest.getId()).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found"));
        if(!existingSubmittedAssignment.getUserId().equals(currentUser.getId())){
            throw new AccessDeniedException("You can only update your own submission");
        }
        if(existingSubmittedAssignment.getGrade()!=null){throw new RuntimeException("Your Submitted Assignment Is Already Graded : UPDATE NOT ALLOWED");}
        var existingFiles=existingSubmittedAssignment.getSubmittedAssignmentFiles();
        for(var existingFile:existingFiles) {
            if (!updateStudentSubmittedAssignmentRequest.getKeepFilesIds().contains(existingFile.getId())) {
                documentMasterService.deleteByDocumentGuide(existingFile.getDocumentGuid());
                submittedAssignmentFileRepo.deleteById(existingFile.getId());
            }
        }
            Iterator<SubmittedAssignmentFile> iterator=existingFiles.iterator();
            while(iterator.hasNext()){
                SubmittedAssignmentFile file=iterator.next();
                if(!updateStudentSubmittedAssignmentRequest.getKeepFilesIds().contains(file.getId())){
                    iterator.remove();
                }
            }
            if(updateStudentSubmittedAssignmentRequest.getSubmittedAssignmentFiles()!=null&& !updateStudentSubmittedAssignmentRequest.getSubmittedAssignmentFiles().isEmpty()){
                List<SubmittedAssignmentFile> newFiles=saveSubmittedAssignmentFiles(updateStudentSubmittedAssignmentRequest.getSubmittedAssignmentFiles(),existingSubmittedAssignment);
                existingFiles.addAll(newFiles);
            }
        SubmittedAssignment updated=submittedAssignmentRepo.save(existingSubmittedAssignment);
        return mapToSubmittedAssignmentResponse(updated);
    }

    @Override
    public List<SubmittedAssignmentResponse> getMySubmissions() {
        User currentUser=getCurrentUser();
        return submittedAssignmentRepo.findByUserId(currentUser.getId()).stream()
                .map(this::mapToSubmittedAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long deleteSubmittedAssignment(Long id) {
        User currentUser=getCurrentUser();
        SubmittedAssignment existingSubmittedAssignment=submittedAssignmentRepo.findById(id).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found"));
        if(!existingSubmittedAssignment.getUserId().equals(currentUser.getId())){
            throw new AccessDeniedException("You can only delete your own submission");
        }
        if(existingSubmittedAssignment.getGrade()!=null){
            throw new RuntimeException("Your Submitted Assignment Is Already Graded: DELETE NOT ALLOWED");
        }
        var existingFiles=existingSubmittedAssignment.getSubmittedAssignmentFiles();
        for(var existingFile:existingFiles){
            documentMasterService.deleteByDocumentGuide(existingFile.getDocumentGuid());
            submittedAssignmentFileRepo.deleteById(existingFile.getId());
        }
        submittedAssignmentRepo.deleteById(id);
        return existingSubmittedAssignment.getAssignment().getId();
    }

    private List<SubmittedAssignmentFile> saveSubmittedAssignmentFiles(List<MultipartFile> files, SubmittedAssignment submittedAssignment)throws IOException {
        List<SubmittedAssignmentFile> submittedAssignmentFiles=new ArrayList<>();
        for(MultipartFile file: files){
            if(file!=null && !file.isEmpty()) {
                SubmittedAssignmentFile submittedAssignmentFile = new SubmittedAssignmentFile();
                DocumentMasterResponse documentMasterResponse=documentMasterService.createFile(file, "assignment_file");
                submittedAssignmentFile.setDocumentGuid(documentMasterResponse.getDocumentGuid());
                submittedAssignmentFile.setFileName(documentMasterResponse.getFileName());
                submittedAssignmentFile.setSubmittedAssignment(submittedAssignment);
                submittedAssignmentFiles.add(submittedAssignmentFileRepo.save(submittedAssignmentFile));
            }
        }
        return submittedAssignmentFiles;

    }
    private SubmittedAssignmentResponse mapToSubmittedAssignmentResponse(SubmittedAssignment submittedAssignment){
        User user=userRepo.findById(submittedAssignment.getUserId()).orElseThrow(()->new RuntimeException("User Not Found"));
        SubmittedAssignmentResponse submittedAssignmentResponse=new SubmittedAssignmentResponse();
        submittedAssignmentResponse.setId(submittedAssignment.getId());
        submittedAssignmentResponse.setUserName(user.getName());
        submittedAssignmentResponse.setUserId(submittedAssignment.getUserId());
        submittedAssignmentResponse.setAssignmentId(submittedAssignment.getAssignment().getId());
        submittedAssignmentResponse.setAssignmentName(submittedAssignment.getAssignment().getTitle());
        submittedAssignmentResponse.setGrade(submittedAssignment.getGrade());
        submittedAssignmentResponse.setFeedBack(submittedAssignment.getFeedBack());
        List<FileResponse> fileResponses=new ArrayList<>();
        for(SubmittedAssignmentFile submittedAssignmentFile:submittedAssignment.getSubmittedAssignmentFiles()){
            FileResponse fileResponse=new FileResponse();
            fileResponse.setId(submittedAssignmentFile.getId());
            fileResponse.setDocumentGuide(submittedAssignmentFile.getDocumentGuid());
            fileResponse.setFileName(submittedAssignmentFile.getFileName());
            fileResponses.add(fileResponse);
        }
        submittedAssignmentResponse.setSubmittedAssignmentFiles(fileResponses);
        return submittedAssignmentResponse;
    }


}
