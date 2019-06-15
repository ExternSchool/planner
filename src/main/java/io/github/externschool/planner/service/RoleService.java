package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.Role;

import java.util.List;

public interface RoleService {
    Role getRoleByName (String name);

    List<Role> getAllRoles();
}
