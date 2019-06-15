package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.externschool.planner.util.Constants.FAKE_MAIL_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private VerificationKeyRepository keyRepository;
    @Mock private PersonRepository personRepository;
    @Mock private ScheduleService scheduleService;
    private UserService userService;

    @Rule public ExpectedException thrown = ExpectedException.none();

    private User expectedUser;
    private UserDTO userDTO;
    private final String email = "dmytro@gmail.com";
    private final Role role = new Role("ROLE_GUEST");
    private final String password = "pass";

    @Before
    public void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                roleService,
                passwordEncoder,
                keyRepository,
                personRepository,
                scheduleService);

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
        User actualUser = userService.getUserByEmail(email);

        assertThat(actualUser)
                .isNotNull()
                .isEqualTo(expectedUser)
                .isEqualToComparingFieldByField(expectedUser);
    }

    @Test
    public void shouldDeleteEvents_WhenDeleteUser() {
        long id1 = 100500L;
        long id2 = 100501L;
        ScheduleEvent eventOne = ScheduleEvent.builder().withOwner(expectedUser).withId(id1).build();
        ScheduleEvent eventTwo = ScheduleEvent.builder().withOwner(expectedUser).withId(id2).build();

        userService.deleteUser(expectedUser);

        verify(userRepository, times(1)).delete(expectedUser);
    }

    @Test
    public void shouldInvokeScheduleServiceDeleteTemplateById_WhenDeleteUser() {
        Long id = 100500L;
        ScheduleTemplate template = ScheduleTemplate.builder().withId(id).withOwner(expectedUser).build();
        List<ScheduleTemplate> templates = Collections.singletonList(template);

        when(scheduleService.getTemplatesByOwner(expectedUser))
                .thenReturn(templates);

        userService.deleteUser(expectedUser);

        verify(scheduleService, times(1)).deleteTemplateById(template.getId());
    }

    @Test
    public void shouldReturnTrue_whenUserHasRole() {
        Boolean shouldBeTrue = userService.userHasRole(expectedUser, role.getName());

        assertThat(shouldBeTrue)
                .isNotNull()
                .isInstanceOf(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    public void shouldReturnFalse_whenUserHasNoRole() {
        Boolean shouldBeFalse = userService.userHasRole(expectedUser, "ROLE_ADMIN");

        assertThat(shouldBeFalse)
                .isNotNull()
                .isInstanceOf(Boolean.class)
                .isEqualTo(false);
    }

    @Test(expected = EmailExistsException.class)
    public void shouldThrowEmailExistsException_IfUserExists() {
        userService.createNewUser(userDTO);

        thrown.expect(EmailExistsException.class);
        thrown.expectMessage("There is already a user with the email provided");
    }

    @Test
    public void shouldReturnNewUser_whenCreateFakeUserWithStudentVerificationKey() {
        VerificationKey key = new VerificationKey();
        Person person = new Person();
        person.addVerificationKey(key);
        keyRepository.save(key);
        personRepository.save(person);
        String fakeMail = key + "@" + FAKE_MAIL_DOMAIN;

        User user = userService.createAndSaveFakeUserWithKeyAndRoleName(key, "ROLE_STUDENT");

        assertThat(user)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("password")
                .hasFieldOrPropertyWithValue("email", fakeMail)
                .hasNoNullFieldsOrPropertiesExcept("id", "version");
        assertThat(user.getRoles())
                .containsExactly(roleService.getRoleByName("ROLE_STUDENT"));
        assertThat(user.getVerificationKey())
                .hasFieldOrPropertyWithValue("person", person);
    }

    @Test
    public void shouldReturnNewUser_whenCreateFakeUserWithGuestVerificationKey() {
        VerificationKey key = new VerificationKey();
        Person person = new Person();
        person.addVerificationKey(key);
        keyRepository.save(key);
        personRepository.save(person);
        String fakeMail = key + "@" + FAKE_MAIL_DOMAIN;

        User user = userService.createAndSaveFakeUserWithKeyAndRoleName(key, "ROLE_GUEST");

        assertThat(user)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("password")
                .hasFieldOrPropertyWithValue("email", fakeMail)
                .hasNoNullFieldsOrPropertiesExcept("id", "version");
        assertThat(user.getRoles())
                .containsExactly(roleService.getRoleByName("ROLE_GUEST"));
        assertThat(user.getVerificationKey())
                .hasFieldOrPropertyWithValue("person", person);
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
    public void shouldReturnThreeRolesUser_whenAssignNewRoleOfTeacherOrOfficialOrAdmin() {
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
    public void shouldAssignTeacherOnlyRole_whenKeyBelongsToTeacherNotOfficial() {
        User actualUser = new User();
        Teacher teacher = new Teacher();
        teacher.setOfficial("");
        VerificationKey key = new VerificationKey();
        teacher.addVerificationKey(key);

        userService.assignNewRolesByKey(actualUser, key);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles", Collections.singleton(new Role("ROLE_TEACHER")));
    }

    @Test
    public void shouldAssignTeacherAndOfficialRole_whenKeyBelongsToTeacherAndOfficial() {
        User actualUser = new User();
        Teacher teacher = new Teacher();
        teacher.setOfficial("Official");
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

        userService.createNewKeyWithNewPersonAndAddToUser(actualUser);

        assertThat(actualUser.getVerificationKey())
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("id", "version");
    }
}
