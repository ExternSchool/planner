package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;

import java.util.List;

public interface StudyPlanService {
    StudyPlan findById(Long id);

    StudyPlan findBySubjectAndGradeLevel(SchoolSubject subject, GradeLevel gradeLevel);

    List<StudyPlan> findAllBySubjectOrderByGradeLevel(SchoolSubject subject);

    List<StudyPlan> findAllByGradeLevelOrderBySubject(GradeLevel gradeLevel);

    List<StudyPlan> findAllByOrderByGradeLevel();

    StudyPlan saveOrUpdatePlan(StudyPlan plan);

    void deletePlan(StudyPlan plan);
}
