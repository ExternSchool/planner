package io.github.externschool.planner.service;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestPlannerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ScheduleEventTypeServiceIntegrationTest {

    @Autowired
    private ScheduleEventTypeService eventTypeService;

    @Test
    @Sql("/datasets/scheduleEventType/oneType.sql")
    public void shouldReturnListEventTypes() {

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();

        List<ScheduleEventType> scheduleEventTypes = this.eventTypeService.loadEventTypes();

        assertThat(scheduleEventTypes)
                .isNotNull()
                .containsExactlyInAnyOrder(eventType);
    }
}
