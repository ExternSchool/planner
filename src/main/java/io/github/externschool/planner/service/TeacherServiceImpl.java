package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class TeacherServiceImpl implements TeacherService {
    private TeacherRepository teacherRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
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
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if (teacher != null) {
            for (SchoolSubject subject : new HashSet<>(teacher.getSubjects())) {
                teacher.removeSubject(subject);
            }
            for (Course course : new HashSet<>(teacher.getCourses())) {
                teacher.removeCourse(course);
            }
        }
        teacherRepository.deleteById(id);
    }
}
