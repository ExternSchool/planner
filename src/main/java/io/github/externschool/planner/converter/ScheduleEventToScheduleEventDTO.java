package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalTime;

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
}
