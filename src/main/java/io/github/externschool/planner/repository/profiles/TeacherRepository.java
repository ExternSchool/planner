package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {
}
