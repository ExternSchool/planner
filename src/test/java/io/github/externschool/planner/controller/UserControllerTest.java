package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    //TODO /success is used as only secured access page available for now
    //TODO refactor this to test authorization using another page when present, please
    @Test
    public void shouldReturnsSuccessTemplate_WhenGetRequestAuthorized() throws Exception {
        mockMvc
                .perform(get("/success").with(
                        user("admin@x.com").password("Admin1").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("success"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Successful Sign Up")));
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
    public void shouldRedirectToLogin_WhenPostSignupSuccessful() throws Exception {
        UserDTO user = new UserDTO("aJd4da65dH5d54Dj",
                "user@x.com",
                "(044)222-2222",
                "!Qwert");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(mapUser(user)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void shouldReturnLoginForm_WhenGetLoginRequest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Login Form")));
    }

    private MultiValueMap<String, String> mapUser(UserDTO user) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("verificationKey",user.getVerificationKey());
        map.add("email",user.getEmail());
        map.add("phoneNumber", user.getPhoneNumber());
        map.add("password", user.getPassword());

        return map;
    }
}
