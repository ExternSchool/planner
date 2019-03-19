package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleEventRepositoryIntegrationTest {
    @Autowired private ScheduleEventRepository repository;
    @Autowired private TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private User oneUser;
    private ScheduleEventType oneType;
    private ScheduleEvent oneEvent;

    @Before
    public void setUp() {
        oneUser = new User();
        oneUser.setEmail("user@email.com");
        oneUser.setPassword("TestPassword");
        entityManager.persist(oneUser);

        oneType = new ScheduleEventType();
        oneType.setName("TestEventType");
        oneType.setAmountOfParticipants(1);
        entityManager.persist(oneType);

        oneEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        oneEvent.setOwner(oneUser);
        oneEvent.setType(oneType);
        entityManager.persist(oneEvent);
    }

    @Test
    public void shouldReturnListEvents() {
        List<ScheduleEvent> events = repository.findAll();

        assertThat(events)
                .isNotNull();
        assertThat(events.get(0))
                .isEqualToIgnoringGivenFields(oneEvent, "createdAt", "modifiedAt");
    }

    @Test
    public void shouldReturnListEventsByUserAndTimeStartingEnding() {
        LocalDate date = LocalDate.of(2018,6, 7);
        LocalDateTime starting = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime ending = LocalDateTime.of(date, LocalTime.MAX);
        List<ScheduleEvent> events =
                repository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(oneUser, starting, ending);

        assertThat(events)
                .isNotNull()
                .hasSize(1)
                .containsExactly(oneEvent);
    }

    @Test
    public void shouldReturnListOfEvents_whenFindAllByOwner() {
        List<ScheduleEvent> events = repository.findAllByOwner(oneUser);

        assertThat(events)
                .isNotNull()
                .hasSize(1)
                .containsExactly(oneEvent);
    }

    @Test
    public void shouldReturnListOfEvents_whenFindAllByType() {
        List<ScheduleEvent> events = repository.findAllByType(oneType);

        assertThat(events)
                .isNotNull()
                .hasSize(1)
                .containsExactly(oneEvent);
    }

    @Test
    public void shouldReturnEmptyListOfEvents_whenDeleteById() {
        repository.deleteById(oneEvent.getId());
        List<ScheduleEvent> events = repository.findAllByOwner(oneUser);

        assertThat(events)
                .isNotNull()
                .isEmpty();
    }
}
