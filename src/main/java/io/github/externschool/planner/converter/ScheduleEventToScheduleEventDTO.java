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
                event.getDescription(),
                event.isOpen(),
                event.getType().getName(),
                event.getTitle(),
                event.getCreatedAt());
    }

//    private String addDescriptionToShow(ScheduleEvent event) {
//        // as a description add a list of participants with their grades, if they are students
//        // or add a name of the type for this event
//        // TODO !!!
//        // TODO Compose these descriptions when Events created
//        // TODO Add appropriate studying subjects
//        // TODO !!!
//
//        Set<User> participants = event.getParticipants();
//        if (participants.isEmpty()) {
//
//            return event.getType().getName();
//        }
//        StringBuilder builder = new StringBuilder();
//        participants.forEach(user -> {
//            if (user != null) {
//                VerificationKey key = user.getVerificationKey();
//                if (key != null) {
//                    if (key.getPerson().getClass() == Student.class) {
//                        Student student = (Student) key.getPerson();
//                        builder.append(student.getLastName()).append(" ").append(student.getFirstName());
//                        GradeLevel level = (student).getGradeLevel();
//                        if (level != null) {
//                            builder.append(", ").append(level.getValue());
//                        }
//                    } else {
//                        // TODO should we use an anonymous message? -- Ask Jeeves
//                        Person person = key.getPerson();
//                        builder
//                                .append(person.getLastName())
//                                .append(" ")
//                                .append(person.getFirstName().charAt(0))
//                                .append(".")
//                                .append(person.getPatronymicName().charAt(0))
//                                .append(".: ")
//                                .append(event.getType().getName());
//                    }
//                }
//            }
//        });
//
//        return builder.toString();
//    }
}
