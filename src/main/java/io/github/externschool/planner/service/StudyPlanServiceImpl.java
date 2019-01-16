package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.util.CollatorHolder;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudyPlanServiceImpl implements StudyPlanService {
    private final StudyPlanRepository planRepository;
    private final CourseService courseService;

    @Autowired
    public StudyPlanServiceImpl(final StudyPlanRepository planRepository,
                                final CourseService courseService) {
        this.planRepository = planRepository;
        this.courseService = courseService;
    }

    @Override
    public StudyPlan findById(final Long id) {
        return planRepository.findStudyPlanById(id);
    }

    @Override
    public List<StudyPlan> findAllByGradeLevelAndSubject(final GradeLevel gradeLevel, final SchoolSubject subject) {
        if (gradeLevel != null && subject != null) {
            return sort(planRepository.findAllByGradeLevelAndSubject(gradeLevel, subject));
        }

        return Collections.emptyList();
    }

    @Override
    public List<StudyPlan> findAllBySubject(final SchoolSubject subject) {
        return Optional.ofNullable(subject).map(planRepository::findAllBySubjectOrderByGradeLevelAscTitleAsc)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<StudyPlan> findAllByGradeLevel(final GradeLevel gradeLevel) {
        return Optional.ofNullable(gradeLevel).map(planRepository::findAllByGradeLevelOrderByTitleAsc)
                .orElseGet(planRepository::findAllByOrderByGradeLevelAscTitleAsc);
    }

    @Override
    public List<StudyPlan> findAll() {
        return sort(planRepository.findAllByOrderByGradeLevelAscTitleAsc());
    }

    @Override
    public StudyPlan saveOrUpdatePlan(final StudyPlan plan) {
        return planRepository.save(plan);
    }

    @Override
    public void deletePlan(final StudyPlan plan) {
        if (plan != null && planRepository.findStudyPlanById(plan.getId()) != null) {
            Optional.ofNullable(plan.getSubject()).ifPresent(subject -> {
                Hibernate.initialize(subject);
                plan.removeSubject();
                planRepository.save(plan);
            });
            courseService.findAllByPlanId(plan.getId()).forEach(courseService::deleteCourse);

            planRepository.delete(plan);
        }
    }

    private List<StudyPlan> sort(List<StudyPlan> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        StudyPlan::getTitle,
                        Comparator.nullsFirst(CollatorHolder.getUaCollator())))
                .collect(Collectors.toList());
    }
}
