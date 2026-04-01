package com.info.ocms.ropository;

import com.info.ocms.model.SubmittedAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmittedAssignmentRepo extends JpaRepository<SubmittedAssignment,Long> {
}
