package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyPlanServiceImpl implements StudyPlanService {
    private final StudyPlanRepository repository;
    private final CourseService courseService;

    @Autowired
    public StudyPlanServiceImpl(final StudyPlanRepository repository, final CourseService courseService) {
        this.repository = repository;
        this.courseService = courseService;
    }

    @Override
    public StudyPlan findById(final Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public StudyPlan findBySubjectAndGradeLevel(final SchoolSubject subject, final GradeLevel gradeLevel) {
        return repository.findBySubjectAndGradeLevel(subject, gradeLevel);
    }

    @Override
    public List<StudyPlan> findAllBySubjectOrderByGradeLevel(final SchoolSubject subject) {
        return repository.findAllBySubjectOrderByGradeLevel(subject);
    }

    @Override
    public List<StudyPlan> findAllByGradeLevelOrderBySubject(final GradeLevel gradeLevel) {
        return repository.findAllByGradeLevelOrderBySubject(gradeLevel);
    }

    @Override
    public List<StudyPlan> findAllByOrderByGradeLevel() {
        return repository.findAllByOrderByGradeLevel();
    }

    @Override
    public StudyPlan saveOrUpdatePlan(final StudyPlan plan) {
        return repository.save(plan);
    }

    @Transactional
    @Override
    public void deletePlan(final StudyPlan plan) {
        if (plan.getSubject() != null) {
            plan.getSubject().removePlan(plan);
        }
        List<Course> courses = courseService.findAllByPlanId(plan.getId());
        if (courses != null) {
            courses.forEach(courseService::deleteCourse);
        }
        repository.delete(plan);
    }
}
