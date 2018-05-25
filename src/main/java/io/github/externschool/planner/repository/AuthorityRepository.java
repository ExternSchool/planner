package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.Authority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, String> {

    Authority findByName(String name);

}
