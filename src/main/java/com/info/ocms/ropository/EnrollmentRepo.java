package com.info.ocms.ropository;

import com.info.ocms.model.Course;
import com.info.ocms.model.Enrollment;
import com.info.ocms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepo extends JpaRepository<Enrollment,Long> {
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByUser(User user);
}
