package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.service.RoleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.github.externschool.planner.util.Constants.UK_ROLE_NAMES;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RoleFormatterTest {
    @Autowired private RoleService roleService;
    @Mock private RoleService service;
    private RoleFormatter formatter;

    private List<Role> roles;

    @Before
    public void setup() {
        formatter = new RoleFormatter(service);
        roles = roleService.getAllRoles();

        for (Role role : roles) {
            Mockito.when(service.getRoleByName(role.getName()))
                    .thenReturn(role);
        }
    }

    @Test
    public void shouldReturnSameRole_whenRunParsePrint() {
        Locale locale = new Locale("uk");

        List<String> ukNames = new ArrayList<>(UK_ROLE_NAMES.values());
        List<Role> actualRoles = new ArrayList<>();
        for (Role role : roles) {
            String name = formatter.print(role, locale);
            actualRoles.add(formatter.parse(name, locale));

            System.out.println(name);
            assertThat(ukNames)
                    .contains(name);
        }

        assertThat(actualRoles)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(roles);
    }
}
