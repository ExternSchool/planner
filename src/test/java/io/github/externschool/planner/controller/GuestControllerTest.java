package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE;
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
@Transactional
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

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private Person person;
    private VerificationKey key;
    private User user;
    private final String userName = "some@email.com";
    private final String personName = "FirstName";
    private MultiValueMap<String, String> map;
    private ScheduleEventType eventType;

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

        key = new VerificationKey();
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

        eventType = new ScheduleEventType();
        eventType.setName("TestType");
        eventType.setAmountOfParticipants(1);
        typeService.saveEventType(eventType);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnGuestList_whenGetGuestWithAdminRole() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list"))
                .andExpect(content().string(Matchers.containsString("Guests List")))
                .andExpect(model().attributeExists("guests"))
                .andExpect(model().attribute("guests",
                        Matchers.hasItem(
                                Matchers.<Person> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(personName)))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnGuestList_whenGetGuestWithSearchWhichDoMatch() throws Exception {
        mockMvc.perform(get("/guest/").param("search", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list"))
                .andExpect(content().string(Matchers.containsString("Guests List")))
                .andExpect(model().attributeExists("guests"))
                .andExpect(model().attribute("guests",
                        Matchers.hasItem(
                                Matchers.<Person> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(personName)))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnEmptyList_whenGetGuestWithSearchWhichNotMatch() throws Exception {
        mockMvc.perform(get("/guest/").param("search", "RequestDoesNotMatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list"))
                .andExpect(content().string(Matchers.containsString("Guests List")))
                .andExpect(model().attribute("guests", Matchers.empty()));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    public void shouldReturnForbidden_whenGetUnauthorized() throws Exception {
        mockMvc.perform(get("/guest/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"TEACHER","ADMIN"})
    public void shouldReturnMaV_whenGetGuestSearch() throws Exception {
        mockMvc.perform(get("/guest/search/" + person.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list"))
                .andExpect(content().string(Matchers.containsString("Guests List")))
                .andExpect(model().attributeExists("guests"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnOk_whenShowCreatePersonProfileModal() throws Exception {
        mockMvc.perform(get("/guest/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list :: createAccount"))
                .andExpect(content().string(Matchers.containsString("createAccountModal")))
                .andExpect(model().attributeExists("guests"))
                .andExpect(model().attribute("person",
                        Matchers.hasProperty("firstName",
                                Matchers.isEmptyOrNullString())));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenProcessCreatePersonProfileModal() throws Exception {
        mockMvc.perform(post("/guest/create").params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(Matchers.containsString("redirect:/guest/")))
                .andExpect(view().name(Matchers.containsString("/official/schedule")));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnOk_whenGetFormPersonProfile() throws Exception {
        mockMvc.perform(get("/guest/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(content().string(Matchers.containsString("Person Profile")))
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

        mockMvc.perform(post("/guest/" + person.getId()).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/person_profile"))
                .andExpect(content().string(Matchers.containsString("Person Profile")))
                .andExpect(model().attributeExists("isNew", "person"))
                .andExpect(model().attribute("person",
                        Matchers.hasProperty("firstName",
                                Matchers.equalToIgnoringCase(personName))));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldRedirect_whenPostUpdateActionSaveGuest() throws Exception {
        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map).with(csrf()))
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
                .params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldKeepOldUserAndKeyAndPerson_whenGuestPostUpdateSaveOldKeyInForm() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        VerificationKey thisKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person thisPerson = new Person();
        User thisUser = userService.createUser("this@mail.com", "pass", "ROLE_GUEST");
        thisPerson.addVerificationKey(thisKey);
        personService.saveOrUpdatePerson(thisPerson);
        thisUser.addVerificationKey(thisKey);
        userService.save(thisUser);

        map = new LinkedMultiValueMap<>();
        map.add("id", thisPerson.getId().toString());
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("verificationKey", thisKey.getValue());

        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map));

        assertThat(userService.getUserByEmail(thisUser.getEmail()))
                .isNotNull();

        assertThat(keyService.findKeyByValue(thisKey.getValue()))
                .isNotNull();

        assertThat(personService.findPersonById(thisPerson.getId()))
                .isNotNull();

        personService.deletePerson(thisPerson);
        userService.deleteUser(thisUser);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRemoveOldKeyAndPerson_whenPostUpdateSaveNewKeyInProfile() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        String otherEmail = "other@mail.com";
        VerificationKey otherKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person otherPerson = new Person();
        User otherUser = userService.createUser(otherEmail, "pass", "ROLE_TEACHER");
        otherPerson.addVerificationKey(otherKey);
        personService.saveOrUpdatePerson(otherPerson);
        otherUser.addVerificationKey(otherKey);
        userService.save(otherUser);

        String thisEmail = "this@mail.com";
        VerificationKey thisKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person thisPerson = new Person();
        User thisUser = userService.createUser(thisEmail, "pass", "ROLE_GUEST");
        thisPerson.addVerificationKey(thisKey);
        personService.saveOrUpdatePerson(thisPerson);
        thisUser.addVerificationKey(thisKey);
        userService.save(thisUser);

        map = new LinkedMultiValueMap<>();
        map.add("id", thisPerson.getId().toString());
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("verificationKey", otherKey.getValue());

        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logout"));

        assertThat(keyService.findKeyByValue(thisKey.getValue()))
                .isNull();

        assertThat(personService.findPersonById(thisPerson.getId()))
                .isNull();

        assertThat(keyService.findKeyByValue(otherKey.getValue()))
                .isNotNull();

        assertThat(personService.findPersonById(otherPerson.getId()))
                .isNotNull();

        personService.deletePerson(otherPerson);
        userService.deleteUser(thisUser);
        keyService.deleteById(otherKey.getId());
    }

    @Test
    @WithMockUser(username = "this@mail.com", roles = "GUEST")
    public void shouldRemoveOldKeyAndPerson_whenGuestPostUpdateSaveNewKeyInProfile() throws Exception {

        String otherEmail = "other@mail.com";
        VerificationKey otherKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person otherPerson = new Person();
        User otherUser = userService.createUser(otherEmail, "pass", "ROLE_TEACHER");
        otherPerson.addVerificationKey(otherKey);
        personService.saveOrUpdatePerson(otherPerson);
        otherUser.addVerificationKey(otherKey);
        userService.save(otherUser);

        String thisEmail = "this@mail.com";
        VerificationKey thisKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person thisPerson = new Person();
        User thisUser = userService.createUser(thisEmail, "pass", "ROLE_GUEST");
        thisPerson.addVerificationKey(thisKey);
        personService.saveOrUpdatePerson(thisPerson);
        thisUser.addVerificationKey(thisKey);
        userService.save(thisUser);

        map = new LinkedMultiValueMap<>();
        map.add("id", thisPerson.getId().toString());
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("verificationKey", otherKey.getValue());

        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logout"));
        //since user is logged out there is no possibility to check user accounts saved

        assertThat(keyService.findKeyByValue(thisKey.getValue()))
                .isNull();

        assertThat(personService.findPersonById(thisPerson.getId()))
                .isNull();

        assertThat(keyService.findKeyByValue(otherKey.getValue()))
                .isNotNull();

        assertThat(personService.findPersonById(otherPerson.getId()))
                .isNotNull();

        personService.deletePerson(otherPerson);
        userService.deleteUser(thisUser);
        keyService.deleteById(otherKey.getId());
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnFormBack_whenPostUpdateActionSaveInvalidKey() throws Exception {
        map.remove("verificationKey");
        map.add("verificationKey", "123");
        mockMvc.perform(post("/guest/update")
                .param("action", "save")
                .params(map).with(csrf()))
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
                .params(map).with(csrf()))
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
                .param("action", "cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldRedirect_whenPostUpdateActionCancelGuest() throws Exception {
        mockMvc.perform(post("/guest/update")
                .param("action", "cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayGuestListFormDeleteModal() throws Exception {
        Long id = person.getId();

        mockMvc.perform(get("/guest/{id}/delete-modal", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_list :: deleteGuest"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenPostDelete() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/guest/" + person.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnForm_whenDisplayOfficialsList() throws Exception {
        mockMvc.perform(get("/guest/official/schedule/"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "officials",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents"));
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnMaV_whenDisplaySubscriptionsToGuest() throws Exception {
        mockMvc.perform(get("/guest/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_subscriptions"))
                .andExpect(model()
                        .attributeExists(
                                "guest",
                                "participants"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenDisplaySubscriptionsToAdmin() throws Exception {
        mockMvc.perform(get("/guest/" + person.getId() + "/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_subscriptions"))
                .andExpect(model()
                        .attributeExists(
                                "guest",
                                "participants"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNullRecentUpdates_whenDisplayOfficialsListToAdmin() throws Exception {
        mockMvc.perform(get("/guest/" + person.getId() + "/official/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "official",
                                "officials",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attributeDoesNotExist("recentUpdate"))
                .andExpect(model()
                        .attribute("availableEvents", 0L));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnNonNullRecentUpdates_whenDisplayOfficialScheduleWithAdmin() throws Exception {
        User eventUser = userService.createUser("teacher@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        eventUser.getRoles().forEach(eventType::addOwner);
        user.getRoles().forEach(eventType::addParticipant);
        typeService.saveEventType(eventType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withEventType(eventType.getName())
                .withDate(scheduleService.getNextWeekFirstDay())
                .withStartTime(LocalTime.of(9, 0))
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);

        scheduleService.removeOwner(event);
        scheduleService.addOwner(eventUser, event);
        assertThat(event.getModifiedAt())
                .isNotNull();

        mockMvc.perform(get("/guest/" + person.getId() + "/official/" + official.getId() + "/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "official",
                                "officials",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attribute("recentUpdate", Matchers.notNullValue()))
                .andExpect(model()
                        .attributeExists("availableEvents"));

        userService.deleteUser(eventUser);
        scheduleService.deleteEventById(event.getId());
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnNullRecentUpdates_whenDisplayOfficialScheduleWithGuest() throws Exception {
        mockMvc.perform(get("/guest/" + person.getId() + "/official/" + person.getId() + "/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "official",
                                "officials",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attributeDoesNotExist("recentUpdate"))
                .andExpect(model()
                        .attribute("availableEvents", 0L));
    }

    @Test
    @WithMockUser(username = userName, roles = {"ADMIN", "GUEST"})
    public void shouldReturnModelAndView_whenDisplaySubscriptionModalWithAdmin() throws Exception {
        User eventUser = userService.createUser("teacher@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        eventUser.getRoles().forEach(eventType::addOwner);
        user.getRoles().forEach(eventType::addParticipant);
        typeService.saveEventType(eventType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withEventType(eventType.getName())
                .withDate(scheduleService.getNextWeekFirstDay())
                .withStartTime(LocalTime.of(9, 0))
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);
        scheduleService.removeOwner(event);
        scheduleService.addOwner(eventUser, event);

        mockMvc.perform(get("/guest/" + person.getId()
                + "/official/" + official.getId() + "/event/" + event.getId() + "/subscribe"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule :: subscribeEvent"))
                .andExpect(model()
                        .attributeExists(
                                "official",
                                "officials",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attribute("recentUpdate", Matchers.notNullValue()))
                .andExpect(model()
                        .attributeExists("availableEvents"));

        userService.deleteUser(eventUser);
        userService.deleteUser(eventUser);
        scheduleService.deleteEventById(event.getId());
    }

    @Test
    @WithMockUser(username = userName, roles = {"ADMIN"})
    public void shouldRedirect_whenSuccessfulProcessNewEventSubscriptionModal() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        VerificationKey otherKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person otherPerson = new Person();
        User otherUser = userService.createUser("other@mail.com", "pass", "ROLE_GUEST");
        otherPerson.addVerificationKey(otherKey);
        personService.saveOrUpdatePerson(otherPerson);
        otherUser.addVerificationKey(otherKey);
        userService.save(otherUser);

        User eventUser = userService.createUser("teacher@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        eventUser.getRoles().forEach(eventType::addOwner);
        user.getRoles().forEach(eventType::addParticipant);
        typeService.saveEventType(eventType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(eventType.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);

        mockMvc.perform(post("/guest/" + otherPerson.getId()
                + "/official/" + official.getId() + "/event/" + event.getId() + "/subscribe").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/" + otherPerson.getId()
                                + "/official/" + official.getId() + "/schedule"));

        personService.deletePerson(otherPerson);
        userService.deleteUser(eventUser);
        userService.deleteUser(otherUser);
        scheduleService.deleteEventById(event.getId());
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnModelAndView_whenUnsuccessfulProcessNewEventSubscriptionModal() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        User eventUser = userService.createUser("off@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));
        official.addVerificationKey(eventUser.getVerificationKey());
        assertThat(keyService.findAll()).contains(official.getVerificationKey());
        assertThat(userService.getUserByEmail(eventUser.getEmail())).isNotNull();
        assertThat(teacherService.findTeacherById(official.getId())).isNotNull();
        long officialId = official.getId();

        VerificationKey otherKey = keyService.saveOrUpdateKey(new VerificationKey());
        Person otherPerson = new Person();
        User otherUser = userService.createUser("other@mail.com", "pass", "ROLE_GUEST");
        otherPerson.addVerificationKey(otherKey);
        personService.saveOrUpdatePerson(otherPerson);
        otherUser.addVerificationKey(otherKey);
        userService.save(otherUser);
        assertThat(keyService.findAll()).contains(otherKey);
        assertThat(userService.getUserByEmail(otherUser.getEmail())).isNotNull()
                .hasFieldOrPropertyWithValue("roles",
                        new HashSet<>(Collections.singletonList(roleService.getRoleByName("ROLE_GUEST"))));
        assertThat(personService.findPersonById(otherPerson.getId())).isNotNull();
        Long guestId = otherPerson.getId();

        ScheduleEventType wrongType = ScheduleEventTypeFactory.createScheduleEventType();
        wrongType.setId(null);
        eventUser.getRoles().forEach(wrongType::addOwner);
        eventUser.getRoles().forEach(wrongType::addParticipant);
        typeService.saveEventType(wrongType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(wrongType.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        eventDTO.setEventType(wrongType.getName());
        ScheduleEvent wrongEvent = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);
        long eid = wrongEvent.getId();

        mockMvc.perform(post("/guest/" + guestId + "/official/"
                        + officialId + "/event/" + eid + "/subscribe").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists("error"))
                .andExpect(model()
                        .attribute("error", Matchers.notNullValue()));

        personService.deletePerson(otherPerson);
        userService.deleteUser(eventUser);
        userService.deleteUser(otherUser);
        scheduleService.deleteEventById(wrongEvent.getId());
        typeService.deleteEventType(wrongType);
    }

    @Test
    @WithMockUser(username = userName, roles = {"GUEST", "ADMIN"})
    public void shouldReturnModelAndView_whenDisplayUnsubscribeModal() throws Exception {
        User eventUser = userService.createUser("teacher@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        eventUser.getRoles().forEach(eventType::addOwner);
        user.getRoles().forEach(eventType::addParticipant);
        typeService.saveEventType(eventType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withEventType(eventType.getName())
                .withDate(scheduleService.getNextWeekFirstDay())
                .withStartTime(LocalTime.of(9, 0))
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);
        scheduleService.removeOwner(event);
        scheduleService.addOwner(eventUser, event);

        scheduleService.getEventsByOwner(eventUser).forEach(System.out::println);

        mockMvc.perform(get("/guest/" + person.getId()
                + "/official/" + official.getId() + "/event/" + event.getId() + "/unsubscribe"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule :: unsubscribe"))
                .andExpect(model()
                        .attributeExists(
                                "official",
                                "officials",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attribute("recentUpdate", Matchers.notNullValue()))
                .andExpect(model()
                        .attributeExists("availableEvents"));

        userService.deleteUser(eventUser);
        scheduleService.deleteEventById(event.getId());
    }

    @Test
    @WithMockUser(username = userName, roles = {"GUEST", "ADMIN"})
    public void shouldRedirect_whenSuccessfulProcessUnsubscribeModalWithAdmin() throws Exception {
        User eventUser = userService.createUser("teacher@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        eventUser.getRoles().forEach(eventType::addOwner);
        user.getRoles().forEach(eventType::addParticipant);
        typeService.saveEventType(eventType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(eventType.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);
        Optional<Participant> participant;
        scheduleService.addParticipant(user, scheduleService.getEventById(event.getId()));

        mockMvc.perform(post("/guest/" + person.getId()
                + "/official/" + official.getId() + "/event/" + event.getId() + "/unsubscribe").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/guest/" + person.getId()
                        + "/official/" + official.getId() + "/schedule"));

        participant = scheduleService.findParticipantByUserAndEvent(eventUser, event);

        assertThat(participant)
                .isEqualTo(Optional.empty());
        assertThat(event.getParticipants())
                .isEmpty();

        scheduleService.deleteEventById(event.getId());
        userService.deleteUser(eventUser);
    }

    @Test
    @WithMockUser(username = userName, roles = "GUEST")
    public void shouldReturnModelAndView_whenUnsuccessfulProcessUnsubscribeModalWithGuest() throws Exception {
        User eventUser = userService.createUser("teacher@mail.co", "pass", "ROLE_OFFICER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_OFFICER");
        Teacher official = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        eventUser.getRoles().forEach(eventType::addOwner);
        typeService.saveEventType(eventType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(eventType.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        eventDTO.setEventType(eventType.getName());
        ScheduleEvent wrongEvent = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);

        mockMvc.perform(post("/guest/" + person.getId() + "/official/"
                + official.getId() + "/event/" + wrongEvent.getId() + "/unsubscribe").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/guest_schedule"))
                .andExpect(model()
                        .attributeExists("error"))
                .andExpect(model()
                        .attribute("error", UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE));

        userService.deleteUser(eventUser);
        scheduleService.deleteEventById(wrongEvent.getId());
    }

    @After
    public void tearDown() {
        Optional.ofNullable(userService.getUserByEmail(user.getEmail()))
                .ifPresent(u -> userService.deleteUser(u));
        Optional.ofNullable(personService.findPersonById(person.getId()))
                .ifPresent(p -> personService.deletePerson(p));
    }
}
