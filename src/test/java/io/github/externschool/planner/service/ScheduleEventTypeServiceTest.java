package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScheduleEventTypeServiceTest {
    @Mock private ScheduleEventTypeRepository eventTypeRepository;
    private ScheduleEventTypeService eventTypeService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.eventTypeService = new ScheduleEventTypeServiceImpl(this.eventTypeRepository);
    }

    @Test
    public void shouldReturnOptionalEventType_whenGetEventTypeById() {
        Optional<ScheduleEventType> expectedType = Optional.of(new ScheduleEventType("Type", 1));
        when(eventTypeRepository.findById(1L))
                .thenReturn(expectedType);

        Optional<ScheduleEventType> actualType = eventTypeService.getEventTypeById(1L);

        assertThat(actualType)
                .isNotNull()
                .isEqualTo(expectedType);
    }

    @Test
    public void shouldReturnEventType_whenSaveEventType() {
        ScheduleEventType expectedType = new ScheduleEventType("Type", 1);
        when(eventTypeRepository.save(expectedType))
                .thenReturn(expectedType);
        when(eventTypeRepository.findByName(expectedType.getName()))
                .thenReturn(null);

        ScheduleEventType actualType = eventTypeService.saveEventType(expectedType);

        assertThat(actualType)
                .isNotNull()
                .isEqualTo(expectedType);
    }

    @Test
    public void shouldReturnUpdatedEventType_whenSaveUpdateEventType() {
        Role role = new Role();
        ScheduleEventType expectedType = new ScheduleEventType("Type", 99);
        expectedType.setId(1L);
        expectedType.addOwner(role);

        when(eventTypeRepository.save(expectedType))
                .thenReturn(expectedType);

        ScheduleEventType actualType = eventTypeService.saveEventType(expectedType);

        assertThat(actualType)
                .isNotNull()
                .isEqualToComparingFieldByFieldRecursively(expectedType);
        System.out.println(expectedType);
    }

    @Test
    public void shouldInvokeOnce_whenDeleteEventType() {
        ScheduleEventType expectedType = new ScheduleEventType("Type", 1);

        eventTypeService.deleteEventType(expectedType);

        verify(eventTypeRepository, times(1)).delete(expectedType);
    }

    @Test
    public void shouldReturnList_whenLoadEventTypes() {

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        when(this.eventTypeRepository.findAll()).thenReturn(Collections.singletonList(eventType));

        List<ScheduleEventType> scheduleEventTypes = this.eventTypeService.loadEventTypes();

        assertThat(scheduleEventTypes)
                .isNotNull()
                .containsExactlyInAnyOrder(eventType);
    }

    @Test
    public void shouldReturnSortedList_whenGetAllEventTypesSorted() {
        ScheduleEventType eventTypeOne = new ScheduleEventType("One", 1);
        ScheduleEventType eventTypeTwo = new ScheduleEventType("Two", 2);
        ScheduleEventType eventTypeOneAndTwo = new ScheduleEventType("OneAndTwo", 10);

        when(eventTypeRepository.findAll())
                .thenReturn(Arrays.asList(eventTypeTwo, eventTypeOne, eventTypeOneAndTwo));

        List<ScheduleEventType> actualEvents = eventTypeService.getAllEventTypesSorted();

        assertThat(actualEvents)
                .isNotEmpty()
                .containsExactly(eventTypeOne, eventTypeOneAndTwo, eventTypeTwo);
    }

    @Test
    public void shouldReturnListsByRoles_whenGetAllEventTypesByUserRoles() {
        Role roleOne = new Role("One");
        Role roleTwo = new Role("Two");
        User userOne = new User();
        userOne.addRole(roleOne);
        User userTwo = new User();
        userTwo.addRole(roleTwo);
        User userOneAndTwo = new User();
        userOneAndTwo.addRole(roleOne);
        userOneAndTwo.addRole(roleTwo);

        ScheduleEventType eventTypeOne = new ScheduleEventType("One", 1);
        eventTypeOne.addOwner(roleOne);
        ScheduleEventType eventTypeTwo = new ScheduleEventType("Two", 2);
        eventTypeTwo.addOwner(roleTwo);
        ScheduleEventType eventTypeOneAndTwo = new ScheduleEventType("OneAndTwo", 10);
        eventTypeOneAndTwo.addOwner(roleOne);
        eventTypeOneAndTwo.addOwner(roleTwo);

        when(eventTypeRepository.findAll())
                .thenReturn(Arrays.asList(eventTypeOne, eventTypeTwo, eventTypeOneAndTwo));

        List<ScheduleEventType> eventTypesOne = eventTypeService.getAllEventTypesByUserRoles(userOne);
        List<ScheduleEventType> eventTypesTwo = eventTypeService.getAllEventTypesByUserRoles(userTwo);
        List<ScheduleEventType> eventTypesOneAndTwo = eventTypeService.getAllEventTypesByUserRoles(userOneAndTwo);
        List<ScheduleEventType> eventTypesZero = eventTypeService.getAllEventTypesByUserRoles(new User());

        assertThat(eventTypesOne)
                .isNotEmpty()
                .containsExactlyInAnyOrder(eventTypeOne, eventTypeOneAndTwo)
                .doesNotContain(eventTypeTwo);

        assertThat(eventTypesTwo)
                .isNotEmpty()
                .containsExactlyInAnyOrder(eventTypeTwo, eventTypeOneAndTwo)
                .doesNotContain(eventTypeOne);

        assertThat(eventTypesOneAndTwo)
                .isNotEmpty()
                .containsExactlyInAnyOrder(eventTypeOne, eventTypeTwo, eventTypeOneAndTwo);

        assertThat(eventTypesZero)
                .isEmpty();
    }
}
