package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class ScheduleEventToScheduleEventDTO implements Converter<ScheduleEvent, ScheduleEventDTO> {
    @Override
    public ScheduleEventDTO convert(ScheduleEvent event) {
        return new ScheduleEventDTO(
                event.getId(),
                LocalDate.from(event.getStartOfEvent()),
                LocalTime.from(event.getStartOfEvent()),
                parts(event),
                event.isOpen(),
                event.getType().getName(),
                event.getTitle(),
                event.getCreatedAt());
    }

    private String parts(ScheduleEvent event) {
        // as a description add a list of participants with their grades, if they are students
        // or add a name of the type for this event
        Set<User> participants = event.getParticipants();
        if (participants.isEmpty()) {

            return event.getType().getName();
        }
        StringBuilder builder = new StringBuilder();
        participants.forEach(user -> {
            if (user != null) {
                VerificationKey key = user.getVerificationKey();
                if (key != null) {
                    Person person = key.getPerson();
                    if (person != null) {
                        builder.append(person.getLastName()).append(" ").append(person.getFirstName());
                        GradeLevel level = ((Student) person).getGradeLevel();
                        if (level != null) {
                            builder.append(", ").append(level.getValue());
                        }
                    }
                }
            }
        });

        return builder.toString();
    }
}
