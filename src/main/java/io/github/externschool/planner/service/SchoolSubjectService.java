package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;

import java.util.List;
import java.util.Optional;

public interface SchoolSubjectService {

    SchoolSubject findSubjectById(Long id);

    List<SchoolSubject> findAll();

    SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject);

    void deleteSubjectFromTeacher(Optional<Teacher> teacher, SchoolSubject schoolSubject);

    void deleteSubjectFromAllTeachers(Optional<List<Teacher>> teachers, SchoolSubject schoolSubject);

    void deleteSubject(Long id);

}
