package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Student;

import java.util.List;

public interface StudentService {

    Student findStudentById(Long id);

    List<Student> findAllStudents();

    List<Student> findAllByOrderByLastNameAsc();

    Student saveOrUpdateStudent(Student student);

    void deleteStudent(Long id);

}
