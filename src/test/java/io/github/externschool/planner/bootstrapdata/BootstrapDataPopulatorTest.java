package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlannerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootstrapDataPopulatorTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;

    @Test
    public void shouldReturnExpectedUser_AfterPropertiesSet() throws Exception {
        String expectedEmail = "q@q";
        Role expectedRole = roleService.getRoleByName("ROLE_ADMIN");
        User actualUser = userRepository.findByEmail(expectedEmail);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrProperty("person")
                .hasFieldOrProperty("email")
                .hasFieldOrProperty("roles")
                .hasFieldOrProperty("password")
                .hasFieldOrPropertyWithValue("email", expectedEmail);
        assertThat(actualUser.getRoles()).contains(expectedRole);
    }
}
