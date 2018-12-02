package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventTypeDTO;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ScheduleEventTypeToDTO implements Converter<ScheduleEventType, ScheduleEventTypeDTO> {
    @Override
    public ScheduleEventTypeDTO convert(final ScheduleEventType eventType) {

        return new ScheduleEventTypeDTO(
                eventType.getId(),
                eventType.getName(),
                eventType.getCountOfParticipant(),
                new ArrayList<>(eventType.getOwners()),
                new ArrayList<>(eventType.getParticipants()));
    }
}
