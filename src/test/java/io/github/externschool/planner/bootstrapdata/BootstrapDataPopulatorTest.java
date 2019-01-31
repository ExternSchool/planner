package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPES;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = PlannerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootstrapDataPopulatorTest {
    @Autowired private UserRepository userRepository;
    @Autowired private RoleService roleService;
    @Autowired private ScheduleEventTypeService typeService;

    @Value("${app.username}") private String inchargeEmail;

    @Test
    public void shouldReturnScheduleEventTypesList_afterPropertiesSet() {
        List<String> typeNames = typeService.loadEventTypes().stream()
                .map(ScheduleEventType::getName)
                .collect(Collectors.toList());

        assertThat(typeNames)
                .containsAll(UK_EVENT_TYPES);
    }

    @Test
    public void shouldReturnExpectedUser_AfterPropertiesSet() {
        String expectedEmail = inchargeEmail;
        Role expectedRole = roleService.getRoleByName("ROLE_ADMIN");
        User actualUser = userRepository.findByEmail(expectedEmail);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrProperty("email")
                .hasFieldOrProperty("roles")
                .hasFieldOrProperty("password")
                .hasFieldOrPropertyWithValue("email", expectedEmail);
        assertThat(actualUser.getRoles()).contains(expectedRole);
    }
}
