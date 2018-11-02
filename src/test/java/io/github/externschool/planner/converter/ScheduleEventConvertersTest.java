package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_PERSONAL;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleEventConvertersTest {
    @Autowired private ConversionService conversionService;

    @Test
    public void shouldReturnExpectedDTO_whenConvertToDTO() {
        Set<User> participants = new HashSet<>();
        User p1 = new User("1", "");
        p1.setId(1L);
        VerificationKey key1 = new VerificationKey();
        p1.addVerificationKey(key1);
        Student s1 = new Student();
        s1.setId(3L);
        s1.setLastName("One");
        s1.setGradeLevel(GradeLevel.LEVEL_7);
        s1.addVerificationKey(key1);
        participants.add(p1);

        ScheduleEvent eventOne = ScheduleEvent.builder()
                .withId(1L)
                .withStartDateTime(LocalDateTime.of(2018, 10, 10, 12, 10))
                .withParticipants(participants)
                .withOpenStatus(false)
                .withType(new ScheduleEventType(UK_EVENT_TYPE_PERSONAL, 1))
                .withTitle("")
                .build();
        ScheduleEvent eventTwo = ScheduleEvent.builder()
                .withId(2L)
                .withStartDateTime(LocalDateTime.of(2018, 10, 10, 12, 40))
                .withParticipants(new HashSet<>())
                .withOpenStatus(true)
                .withType(new ScheduleEventType(UK_EVENT_TYPE_PERSONAL, 1))
                .withTitle("")
                .build();
        List<ScheduleEvent> events = Arrays.asList(eventOne, eventTwo);

        ScheduleEventDTO dtoOne = new ScheduleEventDTO(
                1L,
                LocalDate.from(eventOne.getStartOfEvent()),
                LocalTime.from(eventOne.getStartOfEvent()),
                String.valueOf(s1.getLastName() + " " + s1.getFirstName() + ", " +
                        String.valueOf(s1.getGradeLevel().getValue())),
                false,
                eventOne.getType().getName(),
                eventOne.getTitle(),
                eventOne.getCreatedAt());
        ScheduleEventDTO dtoTwo = new ScheduleEventDTO(
                2L,
                LocalDate.from(eventTwo.getStartOfEvent()),
                LocalTime.from(eventTwo.getStartOfEvent()),
                UK_EVENT_TYPE_PERSONAL,
                true,
                eventTwo.getType().getName(),
                eventTwo.getTitle(),
                eventTwo.getCreatedAt());

        List<ScheduleEventDTO> actualList = events.stream()
                .map(event -> conversionService.convert(event, ScheduleEventDTO.class))
                .collect(Collectors.toList());

        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualToComparingFieldByField(dtoOne);
        assertThat(actualList.get(1))
                .isNotNull()
                .isEqualToComparingFieldByField(dtoTwo);
    }
}
