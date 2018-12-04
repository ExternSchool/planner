package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventTypeDTO;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEventTypeDTOToEventType implements Converter<ScheduleEventTypeDTO, ScheduleEventType> {

    @Override
    public ScheduleEventType convert(final ScheduleEventTypeDTO eventTypeDTO) {
        ScheduleEventType eventType = new ScheduleEventType();
        BeanUtils.copyProperties(eventTypeDTO, eventType, "creators", "participants");
        eventTypeDTO.getOwners().forEach(eventType::addOwner);
        eventTypeDTO.getParticipants().forEach(eventType::addParticipant);

        return eventType;
    }
}
