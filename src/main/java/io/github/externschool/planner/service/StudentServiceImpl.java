package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final VerificationKeyRepository keyRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public StudentServiceImpl(final StudentRepository studentRepository,
                              final VerificationKeyRepository keyRepository,
                              final CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.keyRepository = keyRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Student findStudentById(final Long id) {
        return studentRepository.findStudentById(id);
    }

    @Override
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> findAllByOrderByLastName() {
        return studentRepository.findAllByOrderByLastName();
    }

    @Override
    public List<Student> findAllByGradeLevel(final GradeLevel gradeLevel) {
        return studentRepository.findAllByGradeLevelOrderByLastName(gradeLevel);
    }

    @Override
    public Student saveOrUpdateStudent(final Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    @Override
    public void deleteStudentById(final Long id) {
        Optional.ofNullable(findStudentById(id)).ifPresent(student -> {
            courseRepository.findAllById_StudentIdOrderByTitle(id).stream().filter(Objects::nonNull).forEach(course -> {
                Optional.ofNullable(course.getTeacher()).ifPresent(teacher -> teacher.removeCourse(course));
                courseRepository.delete(course);
            });
            Optional.ofNullable(student.getVerificationKey()).ifPresent(key -> {
                Optional.ofNullable(key.getUser()).ifPresent(User::removeVerificationKey);
                keyRepository.delete(key);
            });
            studentRepository.deleteById(id);
        });
    }
}
