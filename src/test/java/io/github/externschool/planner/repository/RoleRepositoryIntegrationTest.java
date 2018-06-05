package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testTableContainsPresetRoles() {
        List<String> names = new ArrayList<>(Arrays.asList("admin", "guest", "officer", "student", "teacher"));
        List<Role> roles = readRoles();

        assertThat(roles).isNotEmpty();
        assertThat(roles.size() == 5);
        for (String name: names) {
            assertThat(roles.contains(new Role(name)));
        }
    }

    @Test
    public void testCreateReadUpdateDeleteRoles() {
        Role newRole = new Role("anything else");
        roleRepository.save(newRole);
        List<Role> roles = readRoles();

        assertThat(roles.size() == 6);
        assertThat(roles.contains(newRole));

        roleRepository.delete(newRole);
        roles = readRoles();

        assertThat(roles.size() == 5);
        assertThat(!roles.contains(newRole));
    }

    private List<Role> readRoles() {
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().forEach(roles::add);

        return roles;
    }
}
