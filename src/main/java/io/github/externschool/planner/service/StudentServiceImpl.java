package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;
    private VerificationKeyRepository keyRepository;

    public StudentServiceImpl(final StudentRepository studentRepository,
                              final VerificationKeyRepository keyRepository) {
        this.studentRepository = studentRepository;
        this.keyRepository = keyRepository;
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
        return studentRepository.findAllByGradeLevel(gradeLevel);
    }

    @Override
    public Student saveOrUpdateStudent(final Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    @Override
    public void deleteStudentById(final Long id) {
        Student student = findStudentById(id);
        if (student != null) {
            //TODO Clean all fields

            VerificationKey key = student.getVerificationKey();
            if (key != null) {
                if (key.getUser() != null) {
                    key.getUser().removeVerificationKey();
                }
                keyRepository.delete(key);
            }
            studentRepository.deleteById(id);
        }
    }
}
