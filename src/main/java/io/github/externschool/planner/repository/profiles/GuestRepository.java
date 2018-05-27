package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.GuestProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends CrudRepository<GuestProfile, Long> {

}
