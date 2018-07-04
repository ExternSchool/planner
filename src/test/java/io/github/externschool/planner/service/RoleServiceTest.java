package io.github.externschool.planner.service;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.repository.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestPlannerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleServiceTest {

    @Autowired
    private RoleServiceImpl roleService;

    @MockBean
    private RoleRepository roleRepository;

    private Role role;

    @Before
    public void setup(){
        role = new Role("new_admin");
    }

    @Test
    public void getRoleByNameTest(){
        Mockito.when(roleRepository.findByName("new_admin"))
                .thenReturn(role);

        String roleName = "new_admin";

        Role expectedRole = roleService.getRoleByName("new_admin");

        assert(roleName.equals(expectedRole.getName()));
    }

    @Test(expected = RoleNotFoundException.class)
    public void throwException_whenRoleDoesNotExist(){

        Mockito.when(roleRepository.findByName("fake_admin"))
                .thenThrow(RoleNotFoundException.class);

        roleService.getRoleByName("fake_admin");
    }
}
