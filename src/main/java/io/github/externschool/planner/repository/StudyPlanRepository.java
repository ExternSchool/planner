package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    StudyPlan findBySubjectAndGradeLevel(SchoolSubject subject, GradeLevel gradeLevel);

    List<StudyPlan> findAllBySubjectOrderByGradeLevel(SchoolSubject subject);

    List<StudyPlan> findAllByGradeLevelOrderBySubject(GradeLevel gradeLevel);

    List<StudyPlan> findAllByOrderByGradeLevel();
}