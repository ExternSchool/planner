package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.UserDTO;
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

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    public void testGetSuccessAuthorized_ReturnsUserSuccessTemplate() throws Exception {
        mockMvc
                .perform(get("/success").with(
                        user("admin@a").password("Admin1").roles("USER","ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("success"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Successful Sign Up")));
    }

    @Test
    public void testGetSuccessUnauthorized_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/success").with(anonymous()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetSignup_ReturnsUserSignupTemplate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Signup Form")));
    }

    @Test
    public void testPostSignupWithCorrectParameters_ReturnsUserSuccessTemplate() throws Exception {
        UserDTO user = new UserDTO("aJd4da65dH5d54Dj",
                "user@x.com",
                "(044)222-2222",
                "!Qwert");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(mapUser(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("success"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Successful Sign Up")))
                .andExpect(model().attribute("user", equalTo(user)));
    }

    //TODO Browser-side SignUp form validation used
    //TODO This test should fail after DTO validation implemented to avoid fake data in POST requests submission
    @Test
    public void testPostSignupWithInorrectParameters_ReturnsUserSuccessTemplate() throws Exception {
        UserDTO user = new UserDTO("aJd4da65dH5d54Dj",
                "user",
                "999",
                "false");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(mapUser(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("success"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Successful Sign Up")))
                .andExpect(model().attribute("user", equalTo(user)));
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
