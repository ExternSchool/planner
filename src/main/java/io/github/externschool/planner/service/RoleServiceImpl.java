package io.github.externschool.planner.service;

import io.github.externschool.planner.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public io.github.externschool.planner.entity.Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
