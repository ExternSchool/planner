package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;

import java.util.List;

public interface PersonSevice {

    Person saveOrUpdatePerson(Person person);

    Person findPersonById(Long id);

    List<Person> findAll();

    void deletePerson(Long id);


}
