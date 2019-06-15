package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.service.ScheduleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_CONSULT;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ScheduleEventConvertersTest {
    @Autowired private ConversionService conversionService;
    @Autowired private ScheduleService scheduleService;

    @Test
    public void shouldReturnExpectedDTO_whenConvertToDTO() {
        User user = new User("1", "");
        user.setId(1L);
        VerificationKey key1 = new VerificationKey();
        user.addVerificationKey(key1);
        Student s1 = new Student();
        s1.setId(3L);
        s1.setLastName("One");
        s1.setGradeLevel(GradeLevel.LEVEL_7);
        s1.addVerificationKey(key1);

        ScheduleEvent eventOne = ScheduleEvent.builder()
                .withId(1L)
                .withStartDateTime(LocalDateTime.of(2018, 10, 10, 12, 10))
                .withOpenStatus(false)
                .withDescription("One null, 7")
                .withType(new ScheduleEventType(UK_EVENT_TYPE_CONSULT, 1))
                .withTitle("")
                .build();
        scheduleService.addParticipant(user, eventOne);

        ScheduleEvent eventTwo = ScheduleEvent.builder()
                .withId(2L)
                .withStartDateTime(LocalDateTime.of(2018, 10, 10, 12, 40))
                .withOpenStatus(true)
                .withDescription(UK_EVENT_TYPE_CONSULT)
                .withType(new ScheduleEventType(UK_EVENT_TYPE_CONSULT, 1))
                .withTitle("")
                .build();
        List<ScheduleEvent> events = Arrays.asList(eventOne, eventTwo);

        ScheduleEventDTO dtoOne = new ScheduleEventDTO(
                1L,
                LocalDate.from(eventOne.getStartOfEvent()),
                LocalTime.from(eventOne.getStartOfEvent()),
                s1.getLastName() + " " + s1.getFirstName() + ", " + s1.getGradeLevel().getValue(),
                false,
                eventOne.getType().getName(),
                eventOne.getTitle(),
                eventOne.getCreatedAt());
        ScheduleEventDTO dtoTwo = new ScheduleEventDTO(
                2L,
                LocalDate.from(eventTwo.getStartOfEvent()),
                LocalTime.from(eventTwo.getStartOfEvent()),
                UK_EVENT_TYPE_CONSULT,
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
