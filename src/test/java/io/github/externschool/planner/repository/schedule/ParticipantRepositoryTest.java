package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParticipantRepositoryTest {
    @Autowired private ParticipantRepository participantRepository;
    @Autowired TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private User user;
    private ScheduleEvent event;

    @Before
    public void setUp() {
        user = new User("some@email.test", "pass");
        event = ScheduleEvent.builder()
                .withTitle("Title")
                .withStartDateTime(LocalDateTime.now())
                .withEndDateTime(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        entityManager.persist(user);
        entityManager.persist(event);
    }

    @Test
    public void shouldReturnOptionalValue_whenFindParticipantByUserAndEvent() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);

        Optional<Participant> optionalParticipant = participantRepository.findParticipantByUserAndEvent(user, event);

        assertThat(optionalParticipant)
                .isNotEmpty()
                .contains(participant)
                .get()
                .hasFieldOrPropertyWithValue("user", user)
                .hasFieldOrPropertyWithValue("event", event);
    }

    @Test
    public void shouldReturnOptionalEmpty_whenFindParticipantByUserAndEvent() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);
        User unexpectedUser = new User("another@email.test", "pass");
        ScheduleEvent unexpectedEvent = ScheduleEvent.builder()
                .withTitle("Title")
                .withStartDateTime(LocalDateTime.now())
                .withEndDateTime(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        entityManager.persist(unexpectedUser);
        entityManager.persist(unexpectedEvent);

        Optional<Participant> optionalParticipant =
                participantRepository.findParticipantByUserAndEvent(unexpectedUser, unexpectedEvent);

        assertThat(optionalParticipant)
                .isEmpty();
    }

    @Test
    public void shouldReturnOptionalEmpty_whenFindParticipantByInvalidUser() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);
        User unexpectedUser = new User("another@email.test", "pass");
        entityManager.persist(unexpectedUser);

        Optional<Participant> optionalParticipant =
                participantRepository.findParticipantByUserAndEvent(unexpectedUser, event);

        assertThat(optionalParticipant)
                .isEmpty();
    }

    @Test
    public void shouldReturnOptionalEmpty_whenFindParticipantByInvalidEvent() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);
        ScheduleEvent unexpectedEvent = ScheduleEvent.builder()
                .withTitle("Title")
                .withStartDateTime(LocalDateTime.now())
                .withEndDateTime(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        entityManager.persist(unexpectedEvent);

        Optional<Participant> optionalParticipant =
                participantRepository.findParticipantByUserAndEvent(user, unexpectedEvent);

        assertThat(optionalParticipant)
                .isEmpty();
    }

    @Test
    public void shouldReturnParticipantList_whenGetAllByUser() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);

        List<Participant> participantList = participantRepository.getAllByUser(user);

        assertThat(participantList)
                .isNotEmpty()
                .containsExactly(participant);
    }

    @Test
    public void shouldReturnEmptyList_whenGetAllByUser() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);
        User unexpectedUser = new User("another@email.test", "pass");
        entityManager.persist(unexpectedUser);

        List<Participant> participantList =
                participantRepository.getAllByUser(unexpectedUser);

        assertThat(participantList)
                .isEmpty();
    }
}
