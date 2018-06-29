package io.github.externschool.planner.security;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest (classes = TestPlannerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDetailsServiceTest {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    UserRepository userRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserDetails expectedUserDetails;
    private final String userEmail = "dmytro@gmail.com";

    @Before
    public void setUp() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userEmail);
        String userPassword = "password";
        userDTO.setPassword(userPassword);

        expectedUserDetails = new org.springframework.security.core.userdetails.User(
                userDTO.getEmail(),
                userDTO.getPassword(),
                true,
                true,
                true,
                true,
                new HashSet<GrantedAuthority>(Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))));

        User expectedUser = new User();
        expectedUser.setEmail(userEmail);
        expectedUser.setPassword(userPassword);
        expectedUser.addRole(new Role("ROLE_GUEST"));

        Mockito.when(userRepository.findByEmail(expectedUser.getEmail()))
                .thenReturn(expectedUser);
        Mockito.when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);
    }

    @Test
    public void shouldReturnUserDetails_WhenLoadUserByUsername_ThatExists() {
        UserDetails actualDetails = userDetailsService.loadUserByUsername(userEmail);

        assertThat(actualDetails)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedUserDetails, "user");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowUsernameNotFoundException_WhenLoadUserByUsername_ThatDoesNotExist() {
        userDetailsService.loadUserByUsername("something@x.com");

        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("No user found with username: " + "something@x.com");
    }
}
