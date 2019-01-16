package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.StudyPlanDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudyPlanService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_TEST;
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
@Transactional
@SpringBootTest
public class StudyPlanControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private StudyPlanService planService;
    @Autowired private ConversionService conversionService;
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private UserService userService;
    @Autowired private VerificationKeyService keyService;
    private StudyPlanController controller;

    private MockMvc mockMvc;
    private List<StudyPlan> plans;
    private List<SchoolSubject> subjects;
    private Integer originalPlansNumber;
    private User user;
    private static final String USER_NAME = "some@email.com";

    @Before
    public void setup(){
        controller = new StudyPlanController(planService, conversionService, subjectService);
        originalPlansNumber = planService.findAll().size();
        plans = new ArrayList<>();
        subjects = new ArrayList<>();
        GradeLevel level = GradeLevel.LEVEL_7;
        Arrays.asList("Test_History", "Test_Math", "Test_Biology", "Test_English").forEach(title -> {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(title);
            subjectService.saveOrUpdateSubject(subject);
            subjects.add(subject);
            StudyPlan plan = new StudyPlan(level, subject);
            plan.setTitle(title);
            plans.add(planService.saveOrUpdatePlan(plan));
        });

        user = userService.createUser(USER_NAME,"pass", "ROLE_ADMIN");
        user.addVerificationKey(keyService.saveOrUpdateKey(new VerificationKey()));
        userService.save(user);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "GUEST")
    public void shouldReturnForbidden_whenGetUnauthorized() throws Exception {
        mockMvc.perform(get("/plan/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnPlanListTemplate_whenGetAllStudyPlansWithAdminRole() throws Exception {
        List<StudyPlanDTO> expected = planService.findAll().stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.getTitle().isEmpty() && !s.getTitle().equals(UK_EVENT_TYPE_TEST))
                .map(s -> conversionService.convert(s, StudyPlanDTO.class))
                .collect(Collectors.toList());

        mockMvc.perform(get("/plan/"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/plan_list"))
                .andExpect(content().string(Matchers.containsString("Plan List")))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("plans", expected));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnModelAndView_whenGetDisplayStudyPlansListByGrade() throws Exception {
        mockMvc.perform(get("/plan/grade/7"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/plan_list"))
                .andExpect(content().string(Matchers.containsString("Plan List")))
                .andExpect(model().attributeExists("subjects","plans"))
                .andExpect(model().attribute("plans",
                        Matchers.hasItem(
                                Matchers.<StudyPlan> hasProperty("gradeLevel",
                                        Matchers.equalTo(GradeLevel.LEVEL_7)))));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnModelAndView_whenGetDisplayStudyPlansListActionEdit() throws Exception {
        StudyPlanDTO plan = conversionService.convert(plans.get(0), StudyPlanDTO.class);
        Long id = Optional.ofNullable(plan).map(StudyPlanDTO::getId).orElse(0L);

        mockMvc.perform(get("/plan/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/plan_list"))
                .andExpect(content().string(Matchers.containsString("Plan List")))
                .andExpect(model().attributeExists("plans"))
                .andExpect(model().attribute("plan", Matchers.equalTo(plan)));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnModelAndView_whenPostProcessStudyPlansListActionAdd() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        SchoolSubject subject = subjects.get(0);
        map.add("gradeLevel", GradeLevel.LEVEL_7.toString());
        map.add("subject", subject.getId().toString());
        map.add("title", subject.getTitle());
        map.add("action", "add");

        mockMvc.perform(post("/plan/").params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/plan/grade/7"))
                .andExpect(model().attribute("level", 7));

        assertThat(planService.findAllByGradeLevelAndSubject(GradeLevel.LEVEL_7, subject))
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirect_whenPostProcessStudyPlansActionSave() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        StudyPlan plan = plans.get(0);
        SchoolSubject subject = plan.getSubject();
        String newTitle = "New Title instead of " + plan.getTitle();
        map.add("id", plan.getId().toString());
        map.add("gradeLevel", plan.getGradeLevel().toString());
        map.add("subject", subject.getId().toString());
        map.add("title", newTitle);
        map.add("action", "save");
        int previousSize = planService.findAll().size();
        StudyPlan newPlan = new StudyPlan();
        BeanUtils.copyProperties(plan, newPlan);
        newPlan.setTitle(newTitle);

        mockMvc.perform(post("/plan/").params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/plan/grade/7"))
                .andExpect(model().attribute("level", 7));

        assertThat(planService.findAll())
                .hasSize(previousSize)
                .contains(newPlan);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnModelAndView_whenDisplayPlanListDeleteModal() throws Exception {
        mockMvc.perform(get("/plan/" + plans.get(0).getId() + "/delete-modal"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/plan_list :: deleteStudyPlan"))
                .andExpect(model().attributeExists("plan"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirect_whenProcessPlanListDelete() throws Exception {
        mockMvc.perform(post("/plan/" + plans.get(0).getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/plan/"));

        assertThat(planService.findAll())
                .hasSize(originalPlansNumber + 3)
                .doesNotContain(plans.get(0));
    }
}
