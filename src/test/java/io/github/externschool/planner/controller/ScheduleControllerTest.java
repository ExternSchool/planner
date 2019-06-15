package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventTypeDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.UK_ROLE_NAMES;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@WithMockUser(roles = "ADMIN")
public class ScheduleControllerTest {
    @Autowired WebApplicationContext webApplicationContext;
    @MockBean private ScheduleEventTypeService eventTypeService;
    @Mock private ScheduleService scheduleService;
    @Mock private ConversionService conversionService;
    @Autowired private RoleService roleService;
    private ScheduleController scheduleController;
    private MockMvc mockMvc;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private MultiValueMap modelMap;
    private ScheduleEventType eventType;

    @Before
    public void setUp() {
        scheduleController = new ScheduleController(eventTypeService,scheduleService, conversionService, roleService);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        Role roleGuest = roleService.getRoleByName("ROLE_GUEST");

        ScheduleEventTypeDTO eventTypeDTO = new ScheduleEventTypeDTO();
        eventTypeDTO.setId(100L);
        eventTypeDTO.setName("TestType");
        eventTypeDTO.setAmountOfParticipants(1);
        eventTypeDTO.setOwners(Collections.singletonList(roleAdmin));
        eventTypeDTO.setParticipants(Arrays.asList(roleAdmin, roleGuest));

        eventType = new ScheduleEventType();
        eventType.setId(eventTypeDTO.getId());
        eventType.setName(eventTypeDTO.getName());
        eventType.setAmountOfParticipants(eventTypeDTO.getAmountOfParticipants());
        eventTypeDTO.getOwners().forEach(eventType::addOwner);
        eventTypeDTO.getParticipants().forEach(eventType::addParticipant);

        modelMap = new LinkedMultiValueMap<>();
        modelMap.add("id", eventTypeDTO.getId().toString());
        modelMap.add("name", eventTypeDTO.getName());
        modelMap.add("amountOfParticipants", eventTypeDTO.getAmountOfParticipants().toString());
        modelMap.add("owners", UK_ROLE_NAMES.get(roleAdmin.getName()));
        modelMap.add("participants", UK_ROLE_NAMES.get(roleAdmin.getName()));
        modelMap.add("participants", UK_ROLE_NAMES.get(roleGuest.getName()));

        when(conversionService.convert(eventTypeDTO, ScheduleEventType.class))
                .thenReturn(eventType);
        when(conversionService.convert(eventType, ScheduleEventTypeDTO.class))
                .thenReturn(eventTypeDTO);
        when(eventTypeService.getAllEventTypesSorted())
                .thenReturn(Collections.singletonList(eventType));
        when(eventTypeService.getEventTypeById(eventType.getId()))
                .thenReturn(Optional.of(eventType));
    }

    @Test
    public void shouldDisplayForm_whenDisplayEventTypesList() throws Exception {

        mockMvc.perform(get("/event/type/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("ownersRoles", "participantsRoles", "eventType", "eventTypes"))
                .andExpect(view().name("event/event_type"));
    }

    @Test
    public void shouldReturnModelAndView_whenProcessEventTypeAddForm() throws Exception {
        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();

        when(this.eventTypeService.loadEventTypes()).thenReturn(Collections.singletonList(eventType));

        mockMvc.perform(post("/event/type/add")
                .param("new_name", "name").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/event/type/"));
    }

    @Test
    public void shouldRedirect_whenValidData_withProcessEventTypeEditForm() throws Exception {
        mockMvc.perform(post("/event/type/")
                .param("action", "save")
                .params(modelMap).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/event/type/"));
    }

    @Test
    public void shouldReturnSamePage_whenValidationFail_withProcessEventTypeEditForm() throws Exception {
        modelMap.remove("name");
        mockMvc.perform(post("/event/type/")
                .param("action", "save")
                .params(modelMap).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/event_type"));
    }

    @Test
    public void shouldReturnModelAndView_whenDisplayEventTypeIdForm() throws Exception {
        mockMvc.perform(get("/event/type/" + eventType.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/event_type"))
                .andExpect(model()
                        .attributeExists("ownersRoles", "participantsRoles", "eventType", "eventTypes"))
                .andExpect(model()
                        .attribute("eventType",
                                Matchers.hasProperty("id",
                                        Matchers.equalTo(eventType.getId()))))
                .andExpect(model()
                        .attribute("eventType",
                                Matchers.hasProperty("name",
                                        Matchers.equalTo(eventType.getName()))))
                .andExpect(model()
                        .attribute("eventType",
                                Matchers.hasProperty("amountOfParticipants",
                                        Matchers.equalTo(eventType.getAmountOfParticipants()))));
    }

    @Test
    public void shouldRedirect_whenInvalidId_toDisplayEventTypeIdForm() throws Exception {
        mockMvc.perform(get("/event/type/" + 0L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/event/type/"));
    }

    @Test
    public void shouldReturnModelAndView_whenDisplayDeleteEventTypeModal() throws Exception {
        mockMvc.perform(get("/event/type/" + eventType.getId() + "/modal"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/event_type :: deleteEventType"));
    }

    @Test
    public void shouldRedirect_whenProcessDeleteEventType() throws Exception {
        mockMvc.perform(post("/event/type/" + eventType.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/event/type/"));
    }
}
