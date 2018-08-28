package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;

import java.util.List;

public interface StudyPlanService {
    StudyPlan findById(Long id);

    List<StudyPlan> findAllByGradeLevelAndSubject(GradeLevel gradeLevel, SchoolSubject subject);

    List<StudyPlan> findAllBySubject(SchoolSubject subject);

    List<StudyPlan> findAllByGradeLevel(GradeLevel gradeLevel);

    List<StudyPlan> findAll();

    StudyPlan saveOrUpdatePlan(StudyPlan plan);

    void deletePlan(StudyPlan plan);
}
