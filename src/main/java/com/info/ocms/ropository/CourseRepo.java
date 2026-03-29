package com.info.ocms.ropository;

import com.info.ocms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepo extends JpaRepository<Course ,Long> {
}
