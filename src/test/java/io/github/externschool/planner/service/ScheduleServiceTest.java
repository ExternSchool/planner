package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.UserCannotCreateEventException;
import io.github.externschool.planner.factories.RolesFactory;
import io.github.externschool.planner.factories.UserFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class ScheduleServiceTest {

    @Mock
    private ScheduleEventTypeRepository eventTypeRepo;

    @Mock
    private ScheduleEventRepository eventRepo;

    private ScheduleService scheduleSrv;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.scheduleSrv = new ScheduleServiceImpl(this.eventRepo, this.eventTypeRepo);
    }

    @Test
    public void shouldCreateNewScheduleEventIfUserValidForThisEventType() {
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        User user = UserFactory.createUser();
        user.getRoles().add(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));

        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(null);

        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg()).when(this.eventRepo).save(any(ScheduleEvent.class));

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        eventType.getCreators().add(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));
        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(eventType);

        ScheduleEvent event = this.scheduleSrv.createEvent(user, eventReq);

        assertThat(event)
                .isNotNull()
                .isEqualTo(expectedEvent);

    }

    @Test(expected = UserCannotCreateEventException.class)
    public void shouldCreateNewScheduleEventIfUserInValidForThisEventType() {
        User user = UserFactory.createUser();
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(eventType);

        this.scheduleSrv.createEvent(user, eventReq);
    }
}
