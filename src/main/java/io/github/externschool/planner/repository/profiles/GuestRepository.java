package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends CrudRepository<Person, Long> {

}
