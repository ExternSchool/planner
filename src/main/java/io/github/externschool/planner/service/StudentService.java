package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Student;

import java.util.List;

public interface StudentService {

    void deleteStudent(Long id);

    Student findStudentById(Long id);

    Student saveOrUpdateStudent(Student student);

    List<Student> findAllStudents();

    List<Student> findAllByOrderByLastName();
}
