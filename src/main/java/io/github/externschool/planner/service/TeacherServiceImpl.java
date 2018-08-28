package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final VerificationKeyRepository keyRepository;

    @Autowired
    public TeacherServiceImpl(final TeacherRepository teacherRepository,
                              final VerificationKeyRepository keyRepository) {
        this.teacherRepository = teacherRepository;
        this.keyRepository = keyRepository;
    }

    @Override
    public Teacher findTeacherById(Long id) {
        return teacherRepository.findTeacherById(id);
    }

    @Override
    public List<Teacher> findAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public List<Teacher> findAllBySubject(SchoolSubject subject) {
        return teacherRepository.findAllBySubjectsContains(subject);
    }

    @Override
    public List<Teacher> findAllByOrderByLastName() {
        return teacherRepository.findAllByOrderByLastName();
    }

    @Override
    public Teacher saveOrUpdateTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Transactional
    @Override
    public void deleteTeacherById(Long id) {
        Optional.ofNullable(teacherRepository.findTeacherById(id)).ifPresent(teacher -> {
            new HashSet<>(teacher.getCourses()).forEach(teacher::removeCourse);
            new HashSet<>(teacher.getSubjects()).forEach(teacher::removeSubject);
            Optional.ofNullable(teacher.getVerificationKey()).ifPresent(key -> {
                Optional.ofNullable(key.getUser()).ifPresent(User::removeVerificationKey);
                keyRepository.delete(key);
            });
            teacherRepository.deleteById(id);
        });
    }
}
