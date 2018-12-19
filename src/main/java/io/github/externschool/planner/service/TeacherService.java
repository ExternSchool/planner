package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher findTeacherById(Long id);

    List<Teacher> findAllTeachers();

    List<Teacher> findAllBySubject(SchoolSubject subject);

    List<Teacher> findAllByLastName(String lastName);

    List<Teacher> findAllByOrderByLastName();

    Teacher saveOrUpdateTeacher(Teacher teacher);

    void deleteTeacherById(Long id);

    List<Teacher> findAllOfficials();
}
