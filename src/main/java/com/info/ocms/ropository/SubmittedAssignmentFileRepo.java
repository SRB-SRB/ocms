package com.info.ocms.ropository;

import com.info.ocms.model.SubmittedAssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmittedAssignmentFileRepo extends JpaRepository<SubmittedAssignmentFile,Long> {
}
