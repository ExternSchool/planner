package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.profile.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
