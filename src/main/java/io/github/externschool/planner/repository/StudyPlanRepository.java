package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    StudyPlan findStudyPlanById(Long id);

    StudyPlan findByGradeLevelAndSubject(GradeLevel gradeLevel, SchoolSubject subject);

    List<StudyPlan> findAllBySubjectOrderByGradeLevelAscTitleAsc(SchoolSubject subject);

    List<StudyPlan> findAllByGradeLevelOrderByTitleAsc(GradeLevel gradeLevel);

    List<StudyPlan> findAllByOrderByGradeLevelAscTitleAsc();
}
