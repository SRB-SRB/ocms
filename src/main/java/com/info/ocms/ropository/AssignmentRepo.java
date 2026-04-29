package com.info.ocms.ropository;

import com.info.ocms.model.Assignment;
import com.info.ocms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepo extends JpaRepository<Assignment,Long> {
    List<Assignment> findByCourse(Course course);
    void deleteByCourse(Course course);
}
