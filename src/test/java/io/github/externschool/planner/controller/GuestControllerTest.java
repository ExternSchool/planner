package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.service.PersonService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GuestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PersonService personService;

    private MockMvc mockMvc;

    private Person person;

    @Before
    public void setup(){
        person = new Person();
        person.setFirstName("SuchAStrangeLongName");
        personService.saveOrUpdatePerson(person);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnGuestListTemplate_whenGetRequestRootWithAdminRole() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_list"))
                .andExpect(content().string(Matchers.containsString("Guest List")))
                .andExpect(model().attributeExists("persons"))
                .andExpect(model().attribute("persons",
                        Matchers.hasItem(
                                Matchers.<Person> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase("SuchAStrangeLongName")))));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    public void shouldReturnForbidden_wheRequestUnAuthorized() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenRequestPersonAdd() throws Exception {
        mockMvc.perform(post("/guest/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(content().string(Matchers.containsString("Guest Profile")));
    }
}
