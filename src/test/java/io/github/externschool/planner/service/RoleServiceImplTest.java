package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleServiceImplTest {

    @MockBean
    private RoleServiceImpl roleService;

    @Autowired
    private TestEntityManager entityManager;

    Role role;

    @Before
    public void setup(){
        role = new Role("new_admin");
        entityManager.persist(role);
    }

    @Test
    public void getRoleByNameTest(){
        Mockito.when(roleService.getRoleByName("new_admin"))
                .thenReturn(role);
        Role expectedRole = roleService.getRoleByName("new_admin");

        assertThat(expectedRole.getName()).isEqualTo(role.getName());
    }

    @Test(expected = RoleNotFoundException.class)
    public void throwException_whenRoleDoesNotExist(){

        Mockito.when(roleService.getRoleByName("fake_admin"))
                .thenThrow(RoleNotFoundException.class);

        roleService.getRoleByName("fake_admin");
    }
}
