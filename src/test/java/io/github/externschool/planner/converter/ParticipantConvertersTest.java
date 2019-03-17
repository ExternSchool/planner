package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ParticipantDTO;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_CONSULT;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParticipantConvertersTest {
    @Autowired private ConversionService conversionService;

    @Test
    public void shouldReturnExpectedDTO_whenConvertToDTO() {
        User user = new User("1", "");
        user.setId(1L);
        VerificationKey key1 = new VerificationKey();
        user.addVerificationKey(key1);
        Person person = new Person();
        person.setId(2L);
        person.setFirstName("FirstName");
        person.setLastName("LastName");
        person.setPatronymicName("PatronymicName");
        person.addVerificationKey(key1);

        User userOwner = new User("2", "");
        userOwner.setId(3L);
        VerificationKey key2 = new VerificationKey();
        userOwner.addVerificationKey(key2);
        Person personOwner = new Person();
        personOwner.setId(4L);
        personOwner.setFirstName("FirstName");
        personOwner.setLastName("LastName");
        personOwner.setPatronymicName("PatronymicName");
        personOwner.addVerificationKey(key2);

        ScheduleEvent event = ScheduleEvent.builder()
                .withId(5L)
                .withStartDateTime(LocalDateTime.of(2018, 10, 10, 12, 10))
                .withTitle("Title")
                .withDescription("Description")
                .withType(new ScheduleEventType(UK_EVENT_TYPE_CONSULT, 1))
                .withOwner(userOwner)
                .build();
        Participant participant = new Participant(user, event);
        participant.setId(6L);
        participant.setPlanOneId(101L);
        participant.setPlanTwoId(102L);
        participant.setPlanTwoSemesterOne(true);
        participant.setPlanTwoSemesterTwo(true);

        ParticipantDTO dto = conversionService.convert(participant, ParticipantDTO.class);

        assertThat(dto)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", participant.getId())
                .hasFieldOrPropertyWithValue("date", event.getStartOfEvent().toLocalDate())
                .hasFieldOrPropertyWithValue("time", event.getStartOfEvent().toLocalTime())
                .hasFieldOrPropertyWithValue("personName", person.getShortName())
                .hasFieldOrPropertyWithValue("ownerName", personOwner.getShortName())
                .hasFieldOrPropertyWithValue("eventTitle", event.getTitle())
                .hasFieldOrPropertyWithValue("eventDescription", event.getDescription())
                .hasFieldOrPropertyWithValue("personId", person.getId())
                .hasFieldOrPropertyWithValue("ownerId", personOwner.getId())
                .hasFieldOrPropertyWithValue("eventId", event.getId())
                .hasFieldOrPropertyWithValue("planOneId", participant.getPlanOneId())
                .hasFieldOrPropertyWithValue("planOneSemesterOne", false)
                .hasFieldOrPropertyWithValue("planOneSemesterTwo", false)
                .hasFieldOrPropertyWithValue("planTwoId", participant.getPlanTwoId())
                .hasFieldOrPropertyWithValue("planTwoSemesterOne", true)
                .hasFieldOrPropertyWithValue("planTwoSemesterTwo", true);

    }
}
