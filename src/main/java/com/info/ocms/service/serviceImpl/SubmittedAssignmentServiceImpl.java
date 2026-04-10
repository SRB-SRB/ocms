package com.info.ocms.service.serviceImpl;

import com.info.ocms.dto.*;
import com.info.ocms.model.Assignment;
import com.info.ocms.model.AssignmentFile;
import com.info.ocms.model.SubmittedAssignment;
import com.info.ocms.model.SubmittedAssignmentFile;
import com.info.ocms.ropository.AssignmentRepo;
import com.info.ocms.ropository.SubmittedAssignmentFileRepo;
import com.info.ocms.ropository.SubmittedAssignmentRepo;
import com.info.ocms.service.DocumentMasterService;
import com.info.ocms.service.SubmittedAssignmentService;
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
public class SubmittedAssignmentServiceImpl implements SubmittedAssignmentService {
    private final AssignmentRepo assignmentRepo;
    private final SubmittedAssignmentFileRepo submittedAssignmentFileRepo;
    private final DocumentMasterService documentMasterService;
    private final SubmittedAssignmentRepo submittedAssignmentRepo;

    @Override
    public SubmittedAssignmentResponse submitAssignment(StudentSubmittedAssignmentRequest studentSubmittedAssignmentRequest) throws IOException {
        SubmittedAssignment submittedAssignment=new SubmittedAssignment();
        submittedAssignment.setUserId(studentSubmittedAssignmentRequest.getUserId());
        submittedAssignment.setAssignment(assignmentRepo.findById(studentSubmittedAssignmentRequest.getAssignmentId()).orElseThrow(()->new RuntimeException("Assignment Not Found")));
        SubmittedAssignment savedSubmittedAssignment=submittedAssignmentRepo.save(submittedAssignment);
        if (studentSubmittedAssignmentRequest.getSubmittedAssignmentFiles()!=null && !studentSubmittedAssignmentRequest.getSubmittedAssignmentFiles().isEmpty())
        submittedAssignment.setSubmittedAssignmentFiles(saveSubmittedAssignmentFiles(studentSubmittedAssignmentRequest.getSubmittedAssignmentFiles(),savedSubmittedAssignment));
        return mapToSubmittedAssignmentResponse(savedSubmittedAssignment);
    }

    @Override
    public SubmittedAssignmentResponse gradeAssignment(InstructorSubmittedAssignmentRequest instructorSubmittedAssignmentRequest) {
        SubmittedAssignment submittedAssignment=submittedAssignmentRepo.findById(instructorSubmittedAssignmentRequest.getId()).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found"));
        submittedAssignment.setGrade(instructorSubmittedAssignmentRequest.getGrade());
        submittedAssignment.setFeedBack(instructorSubmittedAssignmentRequest.getFeedBack());

        return mapToSubmittedAssignmentResponse(submittedAssignmentRepo.save(submittedAssignment));
    }

    @Override
    public SubmittedAssignmentResponse getSubmittedAssignmentById(Long id) {
        return mapToSubmittedAssignmentResponse(submittedAssignmentRepo.findById(id).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found")));
    }

    @Override
    @Transactional
    public SubmittedAssignmentResponse updateSubmittedAssignment(UpdateStudentSubmittedAssignmentRequest updateStudentSubmittedAssignmentRequest) throws  IOException {
        SubmittedAssignment existingSubmittedAssignment=submittedAssignmentRepo.findById(updateStudentSubmittedAssignmentRequest.getId()).orElseThrow(()->new RuntimeException("Submitted Assignment Not Found"));
        if(existingSubmittedAssignment.getGrade()!=null || existingSubmittedAssignment.getFeedBack()!=null){throw new RuntimeException("Your Submitted Assignment Is Already Graded : UPDATE NOT ALLOWED");}
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

    private List<SubmittedAssignmentFile> saveSubmittedAssignmentFiles(List<MultipartFile> files, SubmittedAssignment submittedAssignment)throws IOException {
        List<SubmittedAssignmentFile> submittedAssignmentFiles=new ArrayList<>();
        for(MultipartFile file: files){
            if(file!=null && !file.isEmpty()) {
                SubmittedAssignmentFile submittedAssignmentFile = new SubmittedAssignmentFile();
                submittedAssignmentFile.setDocumentGuid(documentMasterService.createFile(file, "assignment_file").getDocumentGuid());
                submittedAssignmentFile.setSubmittedAssignment(submittedAssignment);
                submittedAssignmentFiles.add(submittedAssignmentFileRepo.save(submittedAssignmentFile));
            }
        }
        return submittedAssignmentFiles;

    }
    private SubmittedAssignmentResponse mapToSubmittedAssignmentResponse(SubmittedAssignment submittedAssignment){
        SubmittedAssignmentResponse submittedAssignmentResponse=new SubmittedAssignmentResponse();
        submittedAssignmentResponse.setId(submittedAssignment.getId());
        submittedAssignmentResponse.setUserId(submittedAssignmentResponse.getUserId());
        submittedAssignmentResponse.setAssignmentName(submittedAssignment.getAssignment().getTitle());
        submittedAssignmentResponse.setGrade(submittedAssignment.getGrade());
        submittedAssignmentResponse.setFeedBack(submittedAssignment.getFeedBack());
        List<FileResponse> fileResponses=new ArrayList<>();
        for(SubmittedAssignmentFile submittedAssignmentFile:submittedAssignment.getSubmittedAssignmentFiles()){
            FileResponse fileResponse=new FileResponse();
            fileResponse.setId(submittedAssignmentFile.getId());
            fileResponse.setDocumentGuide(submittedAssignmentFile.getDocumentGuid());
            fileResponses.add(fileResponse);
        }
        submittedAssignmentResponse.setSubmittedAssignmentFiles(fileResponses);
        return submittedAssignmentResponse;
    }


}
