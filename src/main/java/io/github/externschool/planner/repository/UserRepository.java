package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
