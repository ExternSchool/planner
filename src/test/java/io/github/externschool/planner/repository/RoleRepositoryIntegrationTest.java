package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryIntegrationTest {
    @Autowired private RoleRepository roleRepository;

    @Test
    public void shouldReturnPresetRoles_WhenFindAllRoles() {
        List<String> names = new ArrayList<>(Arrays.asList(
                "ROLE_ADMIN", "ROLE_GUEST", "ROLE_OFFICER", "ROLE_STUDENT", "ROLE_TEACHER"));
        List<Role> roles = readRoles();

        assertThat(roles)
                .isNotEmpty()
                .size().isEqualTo(5);
        for (String name: names) {
            assertThat(roles).contains(new Role(name));
        }
    }

    @Test
    public void shouldAddOneRole_WhenSaveNewRole() {
        Role newRole = new Role("ROLE_NEW");

        roleRepository.save(newRole);
        List<Role> roles = readRoles();

        assertThat(roles)
                .contains(newRole)
                .size().isEqualTo(6);
    }

    @Test
    public void shouldSubtractOneRole_WhenDeleteRole() {
        Role newRole = new Role("ROLE_NEW");
        roleRepository.save(newRole);
        roleRepository.delete(newRole);
        List<Role> roles = readRoles();

        assertThat(roles)
                .doesNotContain(newRole)
                .size().isEqualTo(5);
    }

    private List<Role> readRoles() {
        return new ArrayList<>(roleRepository.findAll());
    }
}
