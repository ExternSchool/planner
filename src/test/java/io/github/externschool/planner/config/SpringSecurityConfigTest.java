package io.github.externschool.planner.config;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityConfigTest {
    @Autowired private WebApplicationContext wac;
    @Autowired private UserService userService;
    @Mock UserRepository userRepository;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username="admin@com", roles={"ADMIN"})
    public void shouldReturnRedirection_WhenRequestAuthenticatedAndUserAuthorized() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list"))
                .andExpect(model().attributeExists("guests"));
    }

    @Test
    public void shouldReturnRedirection_WhenFormLogout() throws Exception {
        mockMvc.perform(logout())
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @WithAnonymousUser
    public void shouldRedirectToLogin_WhenGetRequestUnauthenticated() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username="q@q", password = "q", roles={"GUEST"})
    public void shouldReturnRedirection_WhenFormLogin() throws Exception {
        Mockito.when(userRepository.findByEmail("q@q"))
                .thenReturn(null);
        User user = userService.createUser("q@q", "q", "ROLE_GUEST");
        userService.save(user);
        Mockito.when(userRepository.findByEmail("q@q"))
                .thenReturn(user);

        mockMvc.perform(formLogin("/login").user("q@q").password("q"))
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/init"));
    }
}
