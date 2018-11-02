package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired private WebApplicationContext wac;
    @Autowired private UserService userService;
    @Autowired private VerificationKeyService keyService;
    @Autowired private ConversionService conversionService;
    private UserController controller;
    private MockMvc mockMvc;
    private User expectedUser;

    @Before
    public void setup() {
        controller = new UserController(userService, keyService, conversionService);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user@x.com", roles = "ADMIN")
    public void shouldReturnsSuccessTemplate_WhenGetRequestAuthorized() throws Exception {
        mockMvc.perform(get("/guest/"))
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
    public void shouldReturnLoginForm_WhenGetLoginRequest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Login Form")));
    }

    @Test
    public void shouldRedirectToLogin_WhenPostSignupParamsOk() throws Exception {
        expectedUser = userService.createUser("user@x.com", "!Qwert", "ROLE_GUEST");
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
    public void shouldRedirectToSignupWithError_WhenInvalidKey() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", "email@email.com");
        map.add("password", "!Qwert");
        map.add("verificationKey", "123");


        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(map))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/signup"))
                .andExpect(model().attribute("error", UK_FORM_INVALID_KEY_MESSAGE));
    }

    @Test
    public void shouldRedirectToSignupWithError_WhenBindingResultHasErrors() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", "");
        map.add("password", "!Qwert");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(map))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/signup"))
                .andExpect(model().attribute("error", UK_FORM_VALIDATION_ERROR_MESSAGE));
    }
}
