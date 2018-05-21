package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;

import java.util.Optional;

public interface UserService {

    void save(User user);

    void delete(Long id);

    Iterable<User> getAll();

    Optional<User> findById(Long id);
}
