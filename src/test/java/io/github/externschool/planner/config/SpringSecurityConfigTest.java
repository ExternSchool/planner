package io.github.externschool.planner.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashSet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class SpringSecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    private UserDetails guestUserDetails;

    @Before
    public void setUp() {
        guestUserDetails = new org.springframework.security.core.userdetails.User(
                "guest@x.com",
                "!Qwert",
                true,
                true,
                true,
                true,
                new HashSet<GrantedAuthority>(Collections.singletonList(new SimpleGrantedAuthority("GUEST"))));
    }

    @Test
    public void shouldReturnAuthenticatedOk_WhenRequestAuthenticatedAndUserAuthorized() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/success")
                .with(user(guestUserDetails));

        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnAuthenticatedForbidden_WhenRequestAuthenticatedAndUserUnauthorized() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/admin/**")
                .with(user(guestUserDetails));

        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldRedirectToLogin_WhenGetRequestUnauthenticated() throws Exception {
        mockMvc.perform(get("/success"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
