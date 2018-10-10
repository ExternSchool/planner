package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 * @author Benkoff (mailto.benkoff@gmal.com)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ScheduleEventRepositoryIntegrationTest {

    @Autowired
    private ScheduleEventRepository repository;

    @Test
    @SqlGroup(
            value = {
                    @Sql("/datasets/user/oneUser.sql"),
                    @Sql("/datasets/scheduleEventType/oneType.sql"),
                    @Sql("/datasets/scheduleEvent/oneEvent.sql")
            }
    )
    public void shouldReturnListEvents() {
        List<ScheduleEvent> events = repository.findAll();

        assertThat(events)
                .isNotNull()
                .containsExactlyInAnyOrder(ScheduleEventFactory.createNewScheduleEventWithoutParticipants());

    }

    @Test
    @SqlGroup(
            value = {
                    @Sql("/datasets/user/oneUser.sql"),
                    @Sql("/datasets/scheduleEventType/oneType.sql"),
                    @Sql("/datasets/scheduleEvent/oneEvent.sql")
            }
    )
    public void shouldReturnListEventsByUserAndTimeStartingEnding() {
        LocalDate date = LocalDate.of(2018,6, 7);
        LocalDateTime starting = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime ending = LocalDateTime.of(date, LocalTime.MAX);
        User owner = new User();
        owner.setId(1L);
        List<ScheduleEvent> events =
                repository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(owner, starting, ending);

        assertThat(events)
                .isNotNull()
                .hasSize(1)
                .containsExactly(repository.getOne(1L));
    }
}
