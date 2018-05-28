package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.TeacherProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends CrudRepository<TeacherProfile, Long> {
}
