package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoleService roleService;
    @InjectMocks private UserService userService = new UserServiceImpl(userRepository, roleService, passwordEncoder);

    @Rule public ExpectedException thrown = ExpectedException.none();

    private User expectedUser;
    private UserDTO userDTO;
    private final String email = "dmytro@gmail.com";
    private final Role role = new Role("ROLE_GUEST");
    private final String password = "pass";

    @Before
    public void setUp() {
        userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setPassword(password);
        expectedUser.addRole(role);

        Mockito.when(userRepository.findByEmail(expectedUser.getEmail())).thenReturn(expectedUser);
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);
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
        User actualUser = userService.findUserByEmail(email);

        assertThat(actualUser)
                .isNotNull()
                .isEqualTo(expectedUser)
                .isEqualToComparingFieldByField(expectedUser);
    }

    @Test(expected = EmailExistsException.class)
    public void shouldThrowEmailExistsException_IfUserExists() {
        userService.createNewUser(userDTO);

        thrown.expect(EmailExistsException.class);
        thrown.expectMessage("There is already a user with the email provided");
    }
}
