package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.RoleInCourse;
import com.info.ocms.model.Course;
import com.info.ocms.model.User;
import com.info.ocms.ropository.EnrollmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursePermissionService {
    private final EnrollmentRepo enrollmentRepo;

    public boolean isCourseInstructor(User user, Course course){
        return enrollmentRepo.findByUserAndCourse(user,course)
                .map(e->e.getRoleInCourse()== RoleInCourse.INSTRUCTOR)
                .orElse(false);
    }
    public boolean isCourseOwner(User user,Course course){
        return enrollmentRepo.findByUserAndCourse(user,course)
                .map(e->e.getRoleInCourse()== RoleInCourse.INSTRUCTOR && e.isOwner())
                .orElse(false);
    }
    public boolean canManageContent(User user,Course course){
        return isCourseInstructor(user,course);
    }
    public boolean canDelete(User user,Course course){
        return isCourseOwner(user,course);
    }
    public boolean canPromote(User user,Course course){
        return isCourseOwner(user,course);
    }

}
