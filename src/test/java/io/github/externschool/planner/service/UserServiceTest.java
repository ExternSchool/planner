package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

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

    @Test
    public void shouldReturnUserWhenCreateNewUser(){
        User actualUser = userService.createNewUser(userDTO);

        System.out.println("USER:" + actualUser.toString());
        System.out.println("PASS:" + actualUser.getPassword().toString());

        assertThat(actualUser)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrProperty("email")
                .hasFieldOrProperty("password");
        assertThat(actualUser.getEmail())
                .isEqualTo(userDTO.getEmail());
        assertThat(actualUser.getPassword())
                .isEqualTo(userDTO.getPassword());
    }

    @Test
    public void shoudReturnUserWhenFindUserByEmail(){
        User actualUser = userService.findUserByEmail(expectedUser.getEmail());

        assertThat(actualUser)
                .isNotNull()
                .isEqualTo(expectedUser)
                .isEqualToComparingFieldByField(expectedUser);
    }
}
