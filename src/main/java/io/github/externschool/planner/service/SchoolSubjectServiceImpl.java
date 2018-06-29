package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchoolSubjectServiceImpl implements SchoolSubjectService {

    private SchoolSubjectRepository subjectRepository;
    private TeacherService teacherService;

    public SchoolSubjectServiceImpl(final SchoolSubjectRepository subjectRepository,
                                    final TeacherService teacherService) {
        this.subjectRepository = subjectRepository;
        this.teacherService = teacherService;
    }

    @Override
    public SchoolSubject findSubjectById(Long id) {
        return subjectRepository.getOne(id);
    }

    @Override
    public List<SchoolSubject> findAllByOrderByNameAsc() {
        return subjectRepository.findAllByOrderByNameAsc();

    }

    @Override
    public SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject) {
        return subjectRepository.save(schoolSubject);
    }


    @Override
    @Transactional
    public void deleteSubject(Long id) {

        List<Teacher> teachers = teacherService.findAllTeachers();

        SchoolSubject subject = subjectRepository.getOne(id);

        for (Teacher teacher: teachers) {
            if (teacher.getSubjects().contains(subject)){
                teacher.getSubjects().remove(subject);
                teacherService.saveOrUpdateTeacher(teacher);
            }
        }

        subjectRepository.delete(subject);
    }
}
