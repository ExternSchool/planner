package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findTeacherById(Long id);

    List<Teacher> findAll();

    List<Teacher> findAllBySubjectsContains(SchoolSubject subject);

    List<Teacher> findAllByOrderByLastName();
}
