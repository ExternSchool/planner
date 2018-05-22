package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.UserServiceImpl;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserServiceImpl userService;


    @Test
    public void whenUserRegistration() {
        User person = new User( "anna@yandex.ru", "password");
        userService.save(person);
//        Person result = this.persons.findByEmail(person.getEmail());
//        assertThat(result.get/Roles().isEmpty(), is(true));
    }

    @Test
    public void registerPOST() throws Exception
    {
        User user = new User("Anna", "pass");
         userService.save(user);

        mockMvc.perform(post("/registration")
                .with(csrf())
                .param("username", user.getEmail())
                .param("password", user.getPassword()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }





    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MainController(userService)).build();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("greeting"))
                .andDo(print());
    }

    @Test
    public void testLoginPageReturnsLoginFormView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Login Form")));
    }

    @Test
    public void testSecuredPageReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isUnauthorized());
    }


}