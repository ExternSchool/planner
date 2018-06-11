package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    List<Person> findAll();
}
