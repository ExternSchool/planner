package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
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
    public void shouldCreateNewScheduleEvent() {

        User user = UserFactory.createUser();
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(null);

        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg()).when(this.eventRepo).save(any(ScheduleEvent.class));

        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(ScheduleEventTypeFactory.createScheduleEventType());

        ScheduleEvent event = this.scheduleSrv.createEvent(user, eventReq);

        assertThat(event)
                .isNotNull()
                .isEqualTo(expectedEvent);

    }
}
