package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
        return createUser(userDTO.getEmail(), userDTO.getPassword(), "ROLE_GUEST");
    }

    @Transactional
    @Override
    public User createUser(String email, String password, String role) throws EmailExistsException {
        if (emailExists(email)) {
            throw new EmailExistsException("There is already a user with the email provided");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.getRoles().add(roleService.getRoleByName(role));

        return saveOrUpdate(user);
    }

    @Override
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }
}
