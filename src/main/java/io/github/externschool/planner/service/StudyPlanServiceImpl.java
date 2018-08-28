package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    public List<StudyPlan> findAllByGradeLevelAndSubject(final GradeLevel gradeLevel, final SchoolSubject subject) {
        if (gradeLevel != null && subject != null) {
            return repository.findAllByGradeLevelAndSubject(gradeLevel, subject);
        }

        return Collections.emptyList();
    }

    @Override
    public List<StudyPlan> findAllBySubject(final SchoolSubject subject) {
        return Optional.ofNullable(subject).map(repository::findAllBySubjectOrderByGradeLevelAscTitleAsc)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<StudyPlan> findAllByGradeLevel(final GradeLevel gradeLevel) {
        return Optional.ofNullable(gradeLevel).map(repository::findAllByGradeLevelOrderByTitleAsc)
                .orElseGet(repository::findAllByOrderByGradeLevelAscTitleAsc);
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
        Optional.ofNullable(plan).ifPresent(p -> {
            Optional.ofNullable(plan.getSubject())
                    .ifPresent(subject -> subject.removePlan(plan));
            courseRepository.findAllById_PlanId(plan.getId()).stream()
                    .filter(Objects::nonNull)
                    .forEach(courseRepository::delete);
            repository.delete(plan);
        });
    }
}
