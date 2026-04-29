package com.info.ocms.ropository;

import com.info.ocms.model.Assignment;
import com.info.ocms.model.SubmittedAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmittedAssignmentRepo extends JpaRepository<SubmittedAssignment,Long> {
    List<SubmittedAssignment> findByAssignment(Assignment assignment);
    Optional<SubmittedAssignment> findByUserIdAndAssignment(Long userId,Assignment assignment);
    List<SubmittedAssignment> findByUserId(Long userId);
    void deleteByAssignment(Assignment assignment);
    Optional<SubmittedAssignment> findByUserIdAndAssignmentId(Long userId,Long assignmentId);
}
