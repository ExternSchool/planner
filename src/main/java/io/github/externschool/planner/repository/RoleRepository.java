package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {

    Role findByName(String name);
}
