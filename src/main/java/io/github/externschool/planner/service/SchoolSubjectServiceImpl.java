package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SchoolSubjectServiceImpl implements SchoolSubjectService {

    private SchoolSubjectRepository subjectRepository;
    private TeacherRepository teacherRepository;

    @Override
    public SchoolSubject findSubjectById(Long id) {
        return subjectRepository.getOne(id);
    }

    @Override
    public List<SchoolSubject> findAll() {
        return subjectRepository.findAll();

    }

    @Override
    public SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject) {
        return subjectRepository.save(schoolSubject);
    }

    @Override
    public void deleteSubjectFromTeacher(Optional<Teacher> teacher, SchoolSubject schoolSubject) {

        Set<SchoolSubject> subjects = teacher.get().getSubjects();
        subjects.remove(schoolSubject);

        teacherRepository.save(teacher.get());

    }

    @Override
    public void deleteSubjectFromAllTeachers(Optional<List<Teacher>> teachers, SchoolSubject schoolSubject) {

        for (Teacher teacher: teachers.get()) {
            if (teacher.getSubjects().contains(schoolSubject)){
                deleteSubjectFromTeacher(Optional.ofNullable(teacher), schoolSubject);
            }
        }

        deleteSubject(schoolSubject.getId());

    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {

        List<Teacher> teachers = teacherRepository.findAll();

        SchoolSubject subject = subjectRepository.getOne(id);

        for (Teacher teacher: teachers) {
            if (teacher.getSubjects().contains(subject)){
                teacher.getSubjects().remove(subject);
                teacherRepository.save(teacher);
            }
        }

        subjectRepository.deleteById(id);
    }
}
