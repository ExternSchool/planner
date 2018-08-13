package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.profile.Student;

import java.util.List;

public interface StudentService {
    Student findStudentById(Long id);

    List<Student> findAllStudents();

    List<Student> findAllByOrderByLastName();

    List<Student> findAllByGradeLevel(GradeLevel gradeLevel);

    Student saveOrUpdateStudent(Student student);

    void deleteStudentById(Long id);
}
