package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends CrudRepository<Profile, Long> {

}
