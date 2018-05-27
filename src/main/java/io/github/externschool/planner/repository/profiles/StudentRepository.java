package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.StudentProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<StudentProfile, Long> {
}
