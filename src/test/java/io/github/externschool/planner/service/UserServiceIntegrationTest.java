package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest {
    @Autowired private UserRepository userRepository;
    @Autowired private RoleService roleService;
    @Autowired private VerificationKeyRepository keyRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ScheduleEventRepository eventRepository;
    @Autowired private ScheduleService scheduleService;
    private UserService userService;

    @Rule public ExpectedException thrown = ExpectedException.none();

    private static final String EMAIL = "some@email.com";
    private static final String PASS = "pass";

    private User expectedUser;
    private Role role;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                roleService,
                passwordEncoder,
                keyRepository,
                personRepository,
                scheduleService);

        role  = roleService.getRoleByName("ROLE_GUEST");
        expectedUser = new User();
        expectedUser.setEmail(EMAIL);
        expectedUser.setPassword(PASS);
        expectedUser.addRole(role);
        userService.save(expectedUser);
    }

    @Test
    public void shouldDeleteUser_WhenDeleteUser() {
        userService.save(expectedUser);

        userService.deleteUser(expectedUser);
        User foundUser = userRepository.findByEmail(EMAIL);

        assertThat(foundUser)
                .isNull();
    }

    @Test
    public void shouldDeleteUserFromEventsOwner_WhenDeleteUser() {
        ScheduleEvent expectedEvent = ScheduleEvent.builder()
                .withOwner(expectedUser)
                .withTitle("An Event")
                .withDescription("Description")
                .withStartDateTime(LocalDateTime.now())
                .withEndDateTime(LocalDateTime.now().plus(Period.of(0, 0, 1)))
                .build();
        eventRepository.save(expectedEvent);
        userService.save(expectedUser);

        userService.deleteUser(expectedUser);
        List<ScheduleEvent> actualEvents = eventRepository.findAllByOwner(expectedUser);

        assertThat(actualEvents)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void shouldDeleteFromParticipants_WhenDeleteUser() {
        User owner = userService.createUser("new@email.com", "pass", "ROLE_ADMIN");
        userService.save(owner);

        ScheduleEvent expectedEvent = ScheduleEvent.builder()
                .withOwner(owner)
                .withTitle("An Event")
                .withDescription("Description")
                .withStartDateTime(LocalDateTime.now())
                .withEndDateTime(LocalDateTime.now().plus(Period.of(0, 0, 1)))
                .withOpenStatus(true)
                .build();
        eventRepository.save(expectedEvent);
        expectedEvent = scheduleService.addParticipant(expectedUser, expectedEvent);

        userService.deleteUser(expectedUser);
        List<ScheduleEvent> actualEvents = eventRepository.findAllByOwner(owner);

        assertThat(actualEvents)
                .isNotNull()
                .isNotEmpty()
                .containsExactly(expectedEvent);

        assertThat(actualEvents.get(0).getParticipants())
                .isEmpty();

        userService.deleteUser(owner);
    }

    @After
    public void tearDown() {
        userService.deleteUser(expectedUser);
    }
}
