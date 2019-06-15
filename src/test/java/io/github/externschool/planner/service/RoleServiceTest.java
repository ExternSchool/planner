package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.repository.RoleRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RoleServiceTest {
    @Mock private RoleRepository roleRepository;
    private RoleService roleService;

    @Rule public ExpectedException thrown = ExpectedException.none();

    private Role role;

    @Before
    public void setup(){
        roleService = new RoleServiceImpl(roleRepository);
        role = new Role("new_admin");
    }

    @Test
    public void getRoleByNameTest(){
        Mockito.when(roleRepository.findByName("new_admin"))
                .thenReturn(role);
        String roleName = "new_admin";

        Role expectedRole = roleService.getRoleByName("new_admin");

        assertThat(roleName)
                .isEqualTo(expectedRole.getName());
    }

    @Test(expected = RoleNotFoundException.class)
    public void throwException_whenRoleDoesNotExist(){
        Mockito.when(roleRepository.findByName("fake_admin"))
                .thenThrow(RoleNotFoundException.class);

        roleService.getRoleByName("fake_admin");

        thrown.expect(RoleNotFoundException.class);
        thrown.expectMessage("");
    }
}
