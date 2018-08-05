package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class ScheduleEventTypeServiceTest {

    @Mock
    private ScheduleEventTypeRepository eventTypeRepo;

    private ScheduleEventTypeService eventTypeService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.eventTypeService = new ScheduleEventTypeServiceImpl(this.eventTypeRepo);
    }

    @Test
    public void shouldReturnListEventTypes() {

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        when(this.eventTypeRepo.findAll()).thenReturn(Collections.singletonList(eventType));

        List<ScheduleEventType> scheduleEventTypes = this.eventTypeService.loadEventTypes();

        assertThat(scheduleEventTypes)
                .isNotNull()
                .containsExactlyInAnyOrder(eventType);
    }
}
