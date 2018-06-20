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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private User expectedUser;
    private UserDTO userDTO;

    @Before
    public void setUp() {
        String email = "dmytro@gmail.com";
        Role role = new Role("ROLE_GUEST");
        String password = "OY&D3e45pieD%JN!F45KSidufh";

        userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setPassword(password);
        expectedUser.addRole(role);

        Mockito.when(userRepository.findByEmail(expectedUser.getEmail()))
                .thenReturn(expectedUser);
        Mockito.when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);
    }

    @Test(expected = EmailExistsException.class)
    public void shouldReturnUser_WhenCreateNewUser(){
        User actualUser = userService.createNewUser(userDTO);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrProperty("email")
                .hasFieldOrProperty("password");
        assertThat(actualUser.getEmail())
                .isEqualTo(userDTO.getEmail());
        assertThat(actualUser.getPassword())
                .isEqualTo(userDTO.getPassword());
        assertTrue(actualUser.getRoles()
                .contains(new Role("ROLE_GUEST")));
    }

    @Test
    public void shouldReturnUser_WhenFindUserByEmail(){
        User actualUser = userService.findUserByEmail(expectedUser.getEmail());

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
