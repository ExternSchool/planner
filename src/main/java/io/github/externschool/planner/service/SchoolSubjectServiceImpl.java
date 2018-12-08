package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import io.github.externschool.planner.util.CollatorHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SchoolSubjectServiceImpl implements SchoolSubjectService {
    private final SchoolSubjectRepository subjectRepository;
    private final StudyPlanService planService;
    private final TeacherRepository teacherRepository;

    @Autowired
    public SchoolSubjectServiceImpl(final SchoolSubjectRepository subjectRepository,
                                    final StudyPlanService planService,
                                    final TeacherRepository teacherRepository) {
        this.subjectRepository = subjectRepository;
        this.planService = planService;
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public SchoolSubject findSubjectById(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public SchoolSubject findSubjectByTitle(final String title) {
        return subjectRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SchoolSubject> findAllByOrderByTitle() {
        return subjectRepository.findAll().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        SchoolSubject::getTitle,
                        Comparator.nullsFirst(CollatorHolder.getUaCollator())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SchoolSubject> findAllById(final List<Long> indices) {
        return subjectRepository.findAllById(indices);
    }

    @Override
    public SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject) {
        return subjectRepository.save(schoolSubject);
    }

    @Override
    public void deleteSubjectById(Long id) {
        SchoolSubject subject = subjectRepository.findById(id).orElse(null);
        if (subject != null) {
            Set<Teacher> teachers = subject.getTeachers();
            for (Teacher teacher : teachers) {
                teacher.removeSubject(subject);
                teacherRepository.save(teacher);
            }
            Set<StudyPlan> plans = subject.getPlans();
            for (StudyPlan plan : plans) {
                plan.removeSubject();
                planService.deletePlan(plan);
            }

            subjectRepository.delete(subject);
        }
    }
}
