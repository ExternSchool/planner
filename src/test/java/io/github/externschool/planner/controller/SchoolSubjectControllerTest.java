package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.service.SchoolSubjectService;
import org.hamcrest.Matchers;
import org.junit.After;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_SUBJECT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
public class SchoolSubjectControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private SchoolSubjectService subjectService;
    private SchoolSubjectController controller;

    private MockMvc mockMvc;
    private List<SchoolSubject> subjects;
    private Integer originalSubjectsNumber;

    @Before
    public void setup(){
        controller = new SchoolSubjectController(subjectService);
        originalSubjectsNumber = subjectService.findAllByOrderByTitle().size();
        subjects = new ArrayList<>();
        Arrays.asList("Test_History", "Test_Math", "Test_Biology", "Test_English").forEach(title -> {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(title);
            subjectService.saveOrUpdateSubject(subject);
            subjects.add(subject);
        });

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "GUEST")
    public void shouldReturnForbidden_whenGetUnauthorized() throws Exception {
        mockMvc.perform(get("/subject/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnSubjectListTemplate_whenGetSubjectWithAdminRole() throws Exception {
        String title = subjects.get(0).getTitle();
        mockMvc.perform(get("/subject/"))
                .andExpect(status().isOk())
                .andExpect(view().name("subject/subject_list"))
                .andExpect(content().string(Matchers.containsString("Subject List")))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("subjects", Matchers.hasSize(4 + originalSubjectsNumber)))
                .andExpect(model().attribute("subjects",
                        Matchers.hasItem(
                                Matchers.<SchoolSubject> hasProperty("title",
                                        Matchers.equalToIgnoringCase(title)))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnSubjectListTemplate_whenPostSubjectListSelectSubject() throws Exception {
        Long id = subjects.get(0).getId();
        mockMvc.perform(post("/subject/" + id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("subject/subject_list"))
                .andExpect(content().string(Matchers.containsString("Subject List")))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("editId", Matchers.equalTo(id)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenPostSubjectListEdit() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String title = "Absolutely New Title";
        map.add("new_title", title);
        Long id = subjects.get(0).getId();

        mockMvc.perform(post("/subject/" + id + "/edit").params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/subject/"));

        assertThat(subjectService.findSubjectById(id))
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", title);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirect_whenPostDelete() throws Exception {
        mockMvc.perform(post("/subject/" + subjects.get(0).getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/subject/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirect_whenPostAdd() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("new_title", "New Title");
        int previousSize = subjectService.findAllByOrderByTitle().size();

        mockMvc.perform(post("/subject/add").params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/subject/"));

        assertThat(subjectService.findAllByOrderByTitle().size())
                .isEqualTo(previousSize + 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndViewWithError_whenPostAddEmptyTitle() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        Integer previousSize = subjectService.findAllByOrderByTitle().size();

        mockMvc.perform(post("/subject/add").params(map).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("subject/subject_list"))
                .andExpect(model().attribute("error", UK_FORM_VALIDATION_ERROR_SUBJECT_MESSAGE));

        assertThat(subjectService.findAllByOrderByTitle().size())
                .isEqualTo(previousSize);
    }

    @After
    public void tearDown() {
        Optional.ofNullable(subjects)
                .ifPresent(subjects ->
                        subjects.forEach(subject ->
                                subjectService.deleteSubjectById(subject.getId())));
    }
}
