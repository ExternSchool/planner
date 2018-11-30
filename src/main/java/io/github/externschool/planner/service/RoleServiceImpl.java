package io.github.externschool.planner.service;

import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public io.github.externschool.planner.entity.Role getRoleByName(String name) throws RoleNotFoundException {

        if (roleRepository.findByName(name) == null){
            throw new RoleNotFoundException("Error");
        }

        return roleRepository.findByName(name);
    }
}
