package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;

    public StudentServiceImpl(final StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void deleteStudent(final Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Student findStudentById(final Long id) {
        return studentRepository.findStudentById(id);
    }

    @Override
    public Student saveOrUpdateStudent(final Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> findAllByOrderByLastName() {
        return studentRepository.findAllByOrderByLastName();
    }
}
