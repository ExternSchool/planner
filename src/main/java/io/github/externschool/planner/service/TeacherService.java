package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;

import java.util.List;

public interface TeacherService {

    void deleteTeacher(Long id);

    Teacher findTeacherById(Long id);

    Teacher saveOrUpdateTeacher(Teacher teacher);

    List<Teacher> findAllTeachers();

    List<Teacher> findAllBySubject(SchoolSubject subject);

    List<Teacher> findAllByOrderByLastNameAsc();
}
