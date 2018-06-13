package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ScheduleEventRepositoryIntegrationTest {

    @Autowired
    private ScheduleEventRepository repo;

    @Test
    @SqlGroup(
            value = {
                    @Sql("/datasets/user/oneUser.sql"),
                    @Sql("/datasets/scheduleEventType/oneType.sql"),
                    @Sql("/datasets/scheduleEvent/oneEvent.sql")
            }
    )
    public void shouldReturnListEvents() {
        List<ScheduleEvent> events = this.repo.findAll();

        assertThat(events)
                .isNotNull()
                .containsExactlyInAnyOrder(ScheduleEventFactory.createNewScheduleEventWithoutParticipants());

    }
}
