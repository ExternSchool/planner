package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findPersonById(Long id);

    Person findPersonByFirstNameAndPatronymicNameAndLastName(
            String firstName,
            String patronymicName,
            String lastName);

    List<Person> findAllByOrderByLastName();
}
