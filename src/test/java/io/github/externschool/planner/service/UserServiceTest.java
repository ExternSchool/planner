package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private VerificationKeyRepository keyRepository;
    @Mock private PersonRepository personRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    private UserService userService;

    @Rule public ExpectedException thrown = ExpectedException.none();

    private User expectedUser;
    private UserDTO userDTO;
    private final String email = "dmytro@gmail.com";
    private final Role role = new Role("ROLE_GUEST");
    private final String password = "pass";

    @Before
    public void setUp() {
        userService =
                new UserServiceImpl(userRepository, roleService, passwordEncoder, keyRepository, personRepository);

        userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setPassword(password);
        expectedUser.addRole(role);

        when(userRepository.findByEmail(expectedUser.getEmail())).thenReturn(expectedUser);
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        Arrays.asList("ROLE_GUEST", "ROLE_STUDENT", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN")
                .forEach(r -> when(roleService.getRoleByName(r))
                        .thenReturn(new Role(r)));
    }

    @Test(expected = EmailExistsException.class)
    public void shouldReturnUserAndGetException_whenCreateUser() {
        User actualUser = userService.createUser(email, password, role.getName());

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("password", passwordEncoder.encode(password))
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(role));
    }

    @Test(expected = EmailExistsException.class)
    public void shouldReturnUserAndGetException_WhenCreateNewUser() {
        User actualUser = userService.createNewUser(userDTO);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("password", passwordEncoder.encode(password))
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(role));
    }

    @Test
    public void shouldReturnUser_WhenFindUserByEmail() {
        User actualUser = userService.findUserByEmail(email);

        assertThat(actualUser)
                .isNotNull()
                .isEqualTo(expectedUser)
                .isEqualToComparingFieldByField(expectedUser);
    }

    @Test(expected = EmailExistsException.class)
    public void shouldThrowEmailExistsException_IfUserExists() {
        userService.createNewUser(userDTO);

        thrown.expect(EmailExistsException.class);
        thrown.expectMessage("There is already a user with the email provided");
    }

    @Test
    public void shouldReturnOneRoleUser_whenAssignNewRoleOfGuest() {
        User actualUser = new User();
        Role actualRole = new Role("ROLE_GUEST");
        Arrays.asList("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN")
                .forEach(r -> actualUser.addRole(new Role(r)));

        userService.assignNewRole(actualUser, actualRole.getName());

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(actualRole));
    }

    @Test
    public void shouldReturnOneRoleUser_whenAssignNewRoleOfStudent() {
        User actualUser = new User();
        Role actualRole = new Role("ROLE_STUDENT");
        Arrays.asList("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN")
                .forEach(r -> actualUser.addRole(new Role(r)));

        userService.assignNewRole(actualUser, actualRole.getName());

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(actualRole));
    }

    @Test
    public void shouldReturnThreeRolesUser_whenAssignNewRoleOfTeacherOrOfficerOrAdmin() {
        final Set<Role> rolesToAssign = Stream.of("ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN")
                .map(Role::new)
                .collect(Collectors.toSet());
        for (Role currentRole : rolesToAssign) {
            User actualUser = new User();
            Arrays.asList("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN")
                    .forEach(givenRole -> actualUser.addRole(new Role(givenRole)));

            userService.assignNewRole(actualUser, currentRole.getName());

            assertThat(actualUser)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("roles", rolesToAssign);
        }
    }

    @Test
    public void shouldAssignStudentRole_whenKeyBelongsToStudent() {
        User actualUser = new User();
        Arrays.asList("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_OFFICER", "ROLE_ADMIN")
                .forEach(r -> actualUser.addRole(new Role(r)));
        Student student = new Student();
        VerificationKey key = new VerificationKey();
        student.addVerificationKey(key);

        userService.assignNewRolesByKey(actualUser, key);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(new Role("ROLE_STUDENT")));
    }

    @Test
    public void shouldAssignTeacherOnlyRole_whenKeyBelongsToTeacherNotOfficer() {
        User actualUser = new User();
        Teacher teacher = new Teacher();
        teacher.setOfficer("");
        VerificationKey key = new VerificationKey();
        teacher.addVerificationKey(key);

        userService.assignNewRolesByKey(actualUser, key);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(new Role("ROLE_TEACHER")));
    }

    @Test
    public void shouldAssignTeacherAndOfficerRole_whenKeyBelongsToTeacherAndOfficer() {
        User actualUser = new User();
        Teacher teacher = new Teacher();
        teacher.setOfficer("Officer");
        VerificationKey key = new VerificationKey();
        teacher.addVerificationKey(key);

        userService.assignNewRolesByKey(actualUser, key);

        assertThat(actualUser)
                .hasFieldOrPropertyWithValue("roles",
                        Stream.of("ROLE_TEACHER", "ROLE_OFFICER").map(Role::new).collect(Collectors.toSet()));
    }

    @Test
    public void shouldAssignGuestRole_whenKeyIsNotOwnedByAnyPerson() {
        User actualUser = new User();
        VerificationKey key = new VerificationKey();
        actualUser.addVerificationKey(key);

        userService.assignNewRolesByKey(actualUser, key);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(new Role("ROLE_GUEST")));
    }

    @Test
    public void shouldReturnNewKeyAndPerson_whenCreateAndAddNewKeyAndPerson() {
        User actualUser = new User();

        userService.createAndAddNewKeyAndPerson(actualUser);

        assertThat(actualUser.getVerificationKey())
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("id");
    }
}
