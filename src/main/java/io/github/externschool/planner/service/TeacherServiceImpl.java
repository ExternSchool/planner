package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public List<Teacher> findAllSortByLastNameAndFirstName() {
        List<Teacher> teachers = teacherRepository.findAll();
        Collections.sort(teachers);
        return teachers;
    }

    @Override
    public Teacher saveOrUpdateTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }
}
