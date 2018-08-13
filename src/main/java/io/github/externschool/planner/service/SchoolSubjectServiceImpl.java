package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchoolSubjectServiceImpl implements SchoolSubjectService {
    private final SchoolSubjectRepository subjectRepository;
    private final StudyPlanService planService;

    @Autowired
    public SchoolSubjectServiceImpl(final SchoolSubjectRepository subjectRepository, final StudyPlanService planService) {
        this.subjectRepository = subjectRepository;
        this.planService = planService;
    }

    @Transactional(readOnly = true)
    @Override
    public SchoolSubject findSubjectById(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public SchoolSubject findSubjectByName(final String name) {
        return subjectRepository.findByTitle(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SchoolSubject> findAllByOrderByName() {
        return subjectRepository.findAllByOrderByTitle();
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

    @Transactional
    @Override
    public void deleteSubjectById(Long id) {
        SchoolSubject subject = subjectRepository.findById(id).orElse(null);
        if (subject != null) {
            if (subject.getPlans() != null) {
                subject.getPlans().forEach(plan -> {
                    subject.removePlan(plan);
                    planService.deletePlan(plan);
                });
            }
            if (subject.getTeachers() != null) {
                for (Teacher teacher : subject.getTeachers()) {
                    teacher.removeSubject(subject);
                }
            }

            subjectRepository.delete(subject);
        }
    }
}
