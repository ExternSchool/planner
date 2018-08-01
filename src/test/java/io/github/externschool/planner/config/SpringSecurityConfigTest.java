package io.github.externschool.planner.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class SpringSecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(username="guest@x.com",roles={"GUEST"})
    public void shouldReturnRedirection_WhenRequestAuthenticatedAndUserAuthorized() throws Exception {
        mockMvc.perform(get("/guest/update"))
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithUserDetails(value="q@q", userDetailsServiceBeanName="userDetailsService")
    public void shouldReturnRedirection_WhenRequestWithUserDetailsService() throws Exception {
        mockMvc.perform(get("/guest/update"))
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithAnonymousUser
    public void shouldRedirectToLogin_WhenGetRequestUnauthenticated() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
