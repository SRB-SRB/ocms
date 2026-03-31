package com.info.ocms.ropository;

import com.info.ocms.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepo extends JpaRepository<Assignment,Long> {
}
