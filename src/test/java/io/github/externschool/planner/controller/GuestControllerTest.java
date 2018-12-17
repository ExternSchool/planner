package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.hamcrest.Matchers;
import org.junit.After;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
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
@AutoConfigureMockMvc
public class GuestControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private PersonService personService;
    @Autowired private UserService userService;
    @Autowired private VerificationKeyService keyService;
    @Autowired private ConversionService conversionService;
    @Autowired private RoleService roleService;
    @Autowired private TeacherService teacherService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private ScheduleEventTypeService typeService;
    private GuestController controller;
    private MockMvc mockMvc;

    private Person person;
    private User user;
    private final String userName = "some@email.com";
    private final String personName = "FirstName";
    private MultiValueMap<String, String> map;

    @Before
    public void setup(){
        controller = new GuestController(
                personService,
                conversionService,
                keyService,
                roleService,
                userService,
                teacherService,
                scheduleService,
                typeService);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        VerificationKey key = new VerificationKey();
        keyService.saveOrUpdateKey(key);

        person = new Person();
        person.setFirstName(personName);
        person.setLastName("A");
        person.setPatronymicName("B");
        person.setPhoneNumber("(000)000-0000");
        person.addVerificationKey(key);
        personService.saveOrUpdatePerson(person);

        PersonDTO personDTO = conversionService.convert(person, PersonDTO.class);
        map = new LinkedMultiValueMap<>();
        map.add("id", personDTO.getId().toString());
        map.add("verificationKey", personDTO.getVerificationKey().getValue());
        map.add("firstName", personDTO.getFirstName());
        map.add("patronymicName", personDTO.getPatronymicName());
        map.add("lastName", personDTO.getLastName());
        map.add("phoneNumber", personDTO.getPhoneNumber());

        user = userService.createUser(userName,"pass", "ROLE_GUEST");
        user.addVerificationKey(key);
        userService.save(user);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnGuestListTemplate_whenGetGuestWithAdminRole() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list"))
                .andExpect(content().string(Matchers.containsString("Guest List")))
                .andExpect(model().attributeExists("guests"))
                .andExpect(model().attribute("guests",
                        Matchers.hasItem(
                                Matchers.<Person> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(personName)))));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    public void shouldReturnForbidden_whenGetUnauthorized() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnOk_whenGetFormPersonProfile() throws Exception {
        mockMvc.perform(get("/guest/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(content().string(Matchers.containsString("Guest Profile")))
                .andExpect(model().attributeExists("isNew", "person"))
                .andExpect(model().attribute("person",
                        Matchers.hasProperty("firstName",
                                Matchers.equalToIgnoringCase(personName))));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnModelAndView_whenPostId() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/guest/" + person.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(content().string(Matchers.containsString("Guest Profile")))
                .andExpect(model().attributeExists("isNew", "person"))
                .andExpect(model().attribute("person",
                        Matchers.hasProperty("firstName",
                                Matchers.equalToIgnoringCase(personName))));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenPostDelete() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/guest/" + person.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldRedirect_whenPostUpdateActionSaveGuest() throws Exception {
        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenPostUpdateActionSaveAdmin() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnFormBack_whenPostUpdateActionSaveInvalidKey() throws Exception {
        map.remove("verificationKey");
        map.add("verificationKey", "123");
        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(model().attribute("error", UK_FORM_INVALID_KEY_MESSAGE));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnFormBack_whenPostUpdateActionSaveEmptyField() throws Exception {
        map.remove("firstName");
        map.add("firstName", "");
        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(model().attribute("error", UK_FORM_VALIDATION_ERROR_MESSAGE));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenPostUpdateActionCancelAdmin() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/guest/update")
                .param("action", "cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldRedirect_whenPostUpdateActionCancelGuest() throws Exception {
        mockMvc.perform(post("/guest/update")
                .param("action", "cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnForm_whenDisplayOfficersList() throws Exception {
        mockMvc.perform(get("/guest/officer/schedule/"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "officers",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenDisplaySubscriptionsToGuest() {
//        @GetMapping("/subscriptions") -> "guest/guest_subscriptions"
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayOfficersListToAdmin() {
//        @PostMapping("/{gid}/officer/schedule") -> prepareModelAndView
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayOfficerScheduleWithAdmin() {
//   @GetMapping("/{gid}/officer/{id}/schedule") -> prepareModelAndView
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenDisplayOfficerScheduleWithGuest() {
//   @GetMapping("/{gid}/officer/{id}/schedule") -> prepareModelAndView
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayNewSubscriptionModalWithAdmin() {
//   @GetMapping("/{gid}/officer/{id}/event/{event}/subscribe") -> prepareModelAndView
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenDisplayNewSubscriptionModalWithGuest() {
//   @GetMapping("/{gid}/officer/{id}/event/{event}/subscribe") -> prepareModelAndView
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnMaV_whenProcessNewEventSubscriptionModalWithAdmin() {
//   @PostMapping("/{gid}/officer/{id}/event/{event}/add") ->
//        redirect:/guest/" + guestId + "/officer/" + officerId + "/schedule"
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenProcessNewEventSubscriptionModalWithGuest() {
//   @PostMapping("/{gid}/officer/{id}/event/{event}/add") ->
//        redirect:/guest/" + guestId + "/officer/" + officerId + "/schedule"
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayCancelSubscriptionModalWithAdmin() {
//   @GetMapping("/{gid}/officer/{id}/event/{event}/cancel") -> "guest/guest_schedule :: cancelSubscription"
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenDisplayCancelSubscriptionModalWithGuest() {
//   @GetMapping("/{gid}/officer/{id}/event/{event}/cancel") -> "guest/guest_schedule :: cancelSubscription"
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnMaV_whenProcessCancelSubscriptionModalWithAdmin() {
//   @PostMapping("/{gid}/officer/{id}/event/{event}/delete") ->
//                "redirect:/guest/" + guestId + "/officer/" + officerId + "/schedule/"
        assertThat(false)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenProcessCancelSubscriptionModalWithGuest() {
//   @PostMapping("/{gid}/officer/{id}/event/{event}/delete") ->
//                "redirect:/guest/" + guestId + "/officer/" + officerId + "/schedule/"
        assertThat(false)
                .isEqualTo(true);
    }

    @After
    public void tearDown() {
        personService.deletePerson(person);
        System.out.println(userService.getUserByEmail(userName));
        userService.deleteUser(user);
        System.out.println(userService.getUserByEmail(userName));
    }
}
