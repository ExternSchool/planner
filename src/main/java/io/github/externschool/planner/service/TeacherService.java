package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Teacher;

import java.util.List;

public interface TeacherService {

    Teacher findTeacherById(Long id);

    List<Teacher> findAllTeachers();

    List<Teacher> findAllSortByLastNameAndFirstName();

    Teacher saveOrUpdateTeacher(Teacher teacher);

    void deleteTeacher(Long id);

}
