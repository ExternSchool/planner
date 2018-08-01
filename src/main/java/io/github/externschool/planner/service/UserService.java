package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;

public interface UserService {
    User findUserByEmail(String email);
    User createNewUser(UserDTO userDTO) throws EmailExistsException;
    User createUser(String email, String password, String role) throws EmailExistsException;
    User saveOrUpdate(User user);
    User assignNewRole(User user, String role) throws RoleNotFoundException;
    User assignNewRolesByKey(User user, VerificationKey key) throws RoleNotFoundException;
}
