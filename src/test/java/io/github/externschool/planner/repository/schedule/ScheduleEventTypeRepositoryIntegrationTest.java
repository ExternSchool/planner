package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ScheduleEventTypeRepositoryIntegrationTest {

    @Autowired
    private ScheduleEventTypeRepository repo;

    @Test
    @Sql("/datasets/scheduleEventType/oneType.sql")
    public void shouldReturnListScheduleEventTypes() {

        List<ScheduleEventType> eventTypes = this.repo.findAll();

        assertThat(eventTypes)
                .isNotNull()
                .containsExactlyInAnyOrder(ScheduleEventTypeFactory.createScheduleEventType());
    }
}
