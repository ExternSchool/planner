package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {

    Person saveOrUpdatePerson(Person person);

    Optional<Person> findPersonById(Long id);

    List<Person> findAllByOrderByNameAsc();

    void deletePerson(Long id);


}
