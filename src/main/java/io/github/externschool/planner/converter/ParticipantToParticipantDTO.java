package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ParticipantDTO;
import io.github.externschool.planner.entity.Participant;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class ParticipantToParticipantDTO implements Converter<Participant, ParticipantDTO> {
    @Override
    public ParticipantDTO convert(final Participant participant) {
        ParticipantDTO participantDTO = new ParticipantDTO(participant.getId());
        BeanUtils.copyProperties(participant, participantDTO, "id");

        return participantDTO;
    }
}
