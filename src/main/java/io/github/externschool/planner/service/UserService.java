package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;

public interface UserService {
    User getUserByEmail(String email);

    User save(User user);

    void deleteUser(User user);

    User createNewUser(UserDTO userDTO) throws EmailExistsException;

    User createUser(String email, String password, String role) throws EmailExistsException;

    Boolean userHasRole(User user, String role);

    User assignNewRole(User user, String role) throws RoleNotFoundException;

    User assignNewRolesByKey(User user, VerificationKey key) throws RoleNotFoundException;

    void createAndAddNewKeyAndPerson(User user);
}
