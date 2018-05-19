package io.github.externschool.planner.web;

import io.github.externschool.planner.dto.UserDTO;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(secure=false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetUserSuccessReturnsUserSuccessTemplate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/success"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Successful Sign Up")))
                .andDo(print());
    }

    @Test
    public void testGetSignupReturnsUserSignupTemplate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/signup"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Signup Form")))
                .andDo(print());
    }

    @Test
    public void testPostSignupReturnsUserSuccessTemplate() throws Exception {
        UserDTO user = new UserDTO("aJd4da65dH5d54Dj",
                "user@mail.com",
                "(044)222-2222",
                "!Qwerty",
                "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("identityKey",user.getIdentityKey());
        map.add("username",user.getUsername());
        map.add("phoneNumber", user.getPhoneNumber());
        map.add("password", user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/signup").params(map))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/success"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Successful Sign Up")))
                .andExpect(model().attribute("user", equalTo(user)));
    }
}
