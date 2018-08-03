package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnsSuccessTemplate_WhenGetRequestAuthorized() throws Exception {
        mockMvc
                .perform(get("/guest/"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_list"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Guest List")));
    }

    @Test
    public void shouldRedirectToLoginError_WhenUserIsNotRegistered() throws Exception {
        mockMvc.perform(formLogin().user("user@x.com").password("!Qwert"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    public void shouldReturnUserSignupForm_WhenGetSignupRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Signup Form")));
    }

    @Test
    public void shouldRedirectToLogin_WhenPostSignupParams() throws Exception {
        User expectedUser = new User("user@x.com", "!Qwert");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email",expectedUser.getEmail());
        map.add("password", expectedUser.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(map))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"))
                .andExpect(view().name("redirect:/login"))
                .andExpect(flash().attributeExists("email"))
                .andExpect(flash().attribute("email", Matchers.equalTo(expectedUser.getEmail())));
    }

    @Test
    public void shouldReturnLoginForm_WhenGetLoginRequest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Login Form")));
    }
}
