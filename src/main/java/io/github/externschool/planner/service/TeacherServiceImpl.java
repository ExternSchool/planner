package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

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
        Teacher teacher = teacherRepository.findTeacherById(id);
        if (teacher != null) {
            for (SchoolSubject subject : new HashSet<>(teacher.getSubjects())) {
                teacher.removeSubject(subject);
            }
            for (Course course : new HashSet<>(teacher.getCourses())) {
                teacher.removeCourse(course);
            }
            VerificationKey key = teacher.getVerificationKey();
            if (key != null) {
                User user = key.getUser();
                if (user != null) {
                    user.removeVerificationKey();
                }
                keyRepository.delete(key);
            }
            teacherRepository.deleteById(id);
        }
    }
}
