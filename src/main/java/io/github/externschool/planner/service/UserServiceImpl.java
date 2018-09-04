package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationKeyRepository keyRepository;
    private final PersonRepository personRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final RoleService roleService,
                           final PasswordEncoder passwordEncoder,
                           final VerificationKeyRepository keyRepository,
                           final PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.keyRepository = keyRepository;
        this.personRepository = personRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(final User user) {
        if (user != null) {
            if (user.getVerificationKey() != null) {
                user.removeVerificationKey();
            }
            userRepository.delete(user);
        }
    }

    @Override
    public User createNewUser(final UserDTO userDTO) throws EmailExistsException {
        return createUser(userDTO.getEmail(), userDTO.getPassword(), "ROLE_GUEST");
    }

    @Override
    public User createUser(String email, String password, String role) throws EmailExistsException {
        if (emailExists(email)) {
            throw new EmailExistsException("This email already has been used");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        assignNewRole(user, role);

        return user;
    }

    @Override
    public User assignNewRole(final User user, final String role) throws RoleNotFoundException {
        if (roleService.getRoleByName(role) == null) {
            throw new RoleNotFoundException("There is no such a role");
        } else {
            List<String> rolesToRemove = new ArrayList<>();
            switch (role) {
                case "ROLE_GUEST":
                    rolesToRemove = Arrays.asList("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN");
                    break;
                case "ROLE_STUDENT":
                    rolesToRemove = Arrays.asList("ROLE_GUEST", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN");
                    break;
                case "ROLE_TEACHER":
                    rolesToRemove = Arrays.asList("ROLE_GUEST", "ROLE_STUDENT");
                    break;
                case "ROLE_OFFICER":
                    rolesToRemove = Arrays.asList("ROLE_GUEST", "ROLE_STUDENT");
                    break;
                case "ROLE_ADMIN":
                    rolesToRemove = Arrays.asList("ROLE_GUEST", "ROLE_STUDENT");
                    break;
            }
            rolesToRemove.stream().map(roleService::getRoleByName).forEach(user::removeRole);
            user.addRole(roleService.getRoleByName(role));
        }

        return user;
    }

    @Override
    public User assignNewRolesByKey(User user, final VerificationKey key) throws RoleNotFoundException {
        if (key == null || key.getPerson() == null || key.getPerson().getClass() == Person.class) {
            user = assignNewRole(user, "ROLE_GUEST");
        } else {
            if (key.getPerson().getClass() == Student.class) {
                user = assignNewRole(user, "ROLE_STUDENT");
            } else {
                user = assignNewRole(user, "ROLE_TEACHER");
                Teacher teacher = (Teacher) key.getPerson();
                if (!teacher.getOfficer().isEmpty()) {
                    user = assignNewRole(user, "ROLE_OFFICER");
                }
            }
        }

        return user;
    }

    @Transactional
    @Override
    public void createAndAddNewKeyAndPerson(User user) {
        //TODO Remove key and person saves when Cascade fixed
        VerificationKey key = new VerificationKey();
        keyRepository.save(key);
        Person person = new Person();
        person.addVerificationKey(key);
        personRepository.save(person);
        user.addVerificationKey(key);
        saveOrUpdate(user);
        assignNewRolesByKey(user, key);
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }
}
