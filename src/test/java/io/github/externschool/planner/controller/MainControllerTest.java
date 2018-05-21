package io.github.externschool.planner.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MainController.class)
@AutoConfigureMockMvc(secure=false)
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLoginPageReturnsLoginFormView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Login Form")));
    }

    @Test
    public void testStartingPageReturnsGreetingView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("greeting"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Extern School")));
    }
}
