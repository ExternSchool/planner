package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {


    User findByEmail(String email);

}
