package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.stereotype.Service;

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
}
