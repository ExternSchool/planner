package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ParticipantRepositoryTest {
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ScheduleEventRepository eventRepository;

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
        userRepository.save(user);
        eventRepository.save(event);
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
        userRepository.save(unexpectedUser);
        eventRepository.save(unexpectedEvent);

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
        userRepository.save(unexpectedUser);

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
        eventRepository.save(unexpectedEvent);

        Optional<Participant> optionalParticipant =
                participantRepository.findParticipantByUserAndEvent(user, unexpectedEvent);

        assertThat(optionalParticipant)
                .isEmpty();
    }

    @Test
    public void shouldReturnParticipantList_whenGetAllByUser() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);

        List<Participant> participantList =
                participantRepository.getAllByUser(user);

        assertThat(participantList)
                .isNotEmpty()
                .containsExactly(participant);
    }

    @Test
    public void shouldReturnEmptyList_whenGetAllByUser() {
        Participant participant = new Participant(user, event);
        participantRepository.save(participant);
        User unexpectedUser = new User("another@email.test", "pass");
        userRepository.save(unexpectedUser);

        List<Participant> participantList =
                participantRepository.getAllByUser(unexpectedUser);

        assertThat(participantList)
                .isEmpty();
    }

    @Repeat(1)
    @Test
    public void shouldReturnSameObject_whenReadFromRepo() {
        ScheduleEvent event = new ScheduleEvent();
        event.setTitle("Event");
        User user = new User();
        Participant participant1 = new Participant(user, event);
        participant1 = participantRepository.save(participant1);
        Participant participant2 = new Participant(user, event);
        participant2 = participantRepository.save(participant2);
        System.out.println(participant1.getEvent().getTitle() + ":" + participant2.getEvent().getTitle());
        Long id1 = participant1.getId();
        Long id2 = participant2.getId();
        List<Participant> readValues = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(100);
        Callable<Optional<Participant>> call1 = () -> {
            return participantRepository.findById(id1);
        };
        Callable<Optional<Participant>> call2 = () -> {
            return participantRepository.findById(id2);
        };

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }

        Future<Optional<Participant>> got1 = executor.submit(call1);
        Future<Optional<Participant>> got2 = executor.submit(call2);

        try {
            Optional<Participant> res1 = got1.get();
            Optional<Participant> res2 = got2.get();

            System.out.println(res1.get().getEvent().getTitle() + ":" + res2.get().getEvent().getTitle());
            assertThat(res1)
                    .matches(Matchers.equalTo(res2));
            assertThat(res1.get().equals(res2.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
