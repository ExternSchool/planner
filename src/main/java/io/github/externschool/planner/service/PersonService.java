package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;

import java.util.List;

public interface PersonService {
    Person saveOrUpdatePerson(Person person);

    Person findPersonById(Long id);

    Person findPersonByFullNameAndPhoneNumber(Person person);

    List<Person> findAllByOrderByName();

    void deletePerson(Person person);
}
