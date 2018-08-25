package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudyPlanServiceImpl implements StudyPlanService {
    private final StudyPlanRepository repository;
    private final CourseRepository courseRepository;

    @Autowired
    public StudyPlanServiceImpl(final StudyPlanRepository repository, final CourseRepository courseRepository) {
        this.repository = repository;
        this.courseRepository = courseRepository;
    }

    @Override
    public StudyPlan findById(final Long id) {
        return repository.findStudyPlanById(id);
    }

    @Override
    public StudyPlan findByGradeLevelAndSubject(final GradeLevel gradeLevel, final SchoolSubject subject) {
        return repository.findByGradeLevelAndSubject(gradeLevel, subject);
    }

    @Override
    public List<StudyPlan> findAllBySubject(final SchoolSubject subject) {
        return repository.findAllBySubjectOrderByGradeLevelAscTitleAsc(subject);
    }

    @Override
    public List<StudyPlan> findAllByGradeLevel(final GradeLevel gradeLevel) {
        if (gradeLevel == null) {
            return findAll();
        }
        return repository.findAllByGradeLevelOrderByTitleAsc(gradeLevel);
    }

    @Override
    public List<StudyPlan> findAll() {
        return repository.findAllByOrderByGradeLevelAscTitleAsc();
    }

    @Override
    public StudyPlan saveOrUpdatePlan(final StudyPlan plan) {
        return repository.save(plan);
    }

    @Transactional
    @Override
    public void deletePlan(final StudyPlan plan) {
        Optional.ofNullable(plan.getSubject())
                .ifPresent(subject -> subject.removePlan(plan));
        courseRepository.findAllById_PlanId(plan.getId()).stream()
                .filter(Objects::nonNull)
                .forEach(courseRepository::delete);
        repository.delete(plan);
    }
}
