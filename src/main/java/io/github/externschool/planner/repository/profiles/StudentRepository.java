package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

    List<Student> findAll();

}
