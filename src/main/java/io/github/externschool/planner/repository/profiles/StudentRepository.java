package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.profile.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
    Student findStudentById(Long id);

    List<Student> findAll();

    List<Student> findAllByOrderByLastName();

    List<Student> findAllByGradeLevelOrderByLastName(GradeLevel gradeLevel);
}
