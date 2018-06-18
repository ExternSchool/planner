package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createNewUser(final UserDTO userDTO) throws EmailExistsException {
        if (emailExists(userDTO)) {
            throw new EmailExistsException("There is already a user with the email provided");
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (user.getRoles().isEmpty()) {
            user.getRoles().add(roleService.getRoleByName("ROLE_GUEST"));
        } 

        return userRepository.save(user);
    }

    private boolean emailExists(final UserDTO userDTO) {
        return userRepository.findByEmail(userDTO.getEmail()) != null;
    }
}
