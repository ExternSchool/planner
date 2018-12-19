package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ParticipantDTO;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;

public class ParticipantToParticipantDTO implements Converter<Participant, ParticipantDTO> {
    @Override
    public ParticipantDTO convert(final Participant participant) {
        Person person = Optional.ofNullable(participant.getUser())
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .orElse(new Person());
        ScheduleEvent event = participant.getEvent();
        Person eventOwner = Optional.ofNullable(event.getOwner())
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .orElse(new Person());
        ParticipantDTO participantDTO = new ParticipantDTO(
                participant.getId(),
                person,
                event);
        participantDTO.setDate(event.getStartOfEvent().toLocalDate());
        participantDTO.setTime(event.getStartOfEvent().toLocalTime());
        participantDTO.setPersonName(person.getShortName());
        participantDTO.setOwnerName(eventOwner.getShortName());
        participantDTO.setEventTitle(event.getTitle());
        participantDTO.setEventDescription(event.getDescription());
        participantDTO.setPersonId(person.getId());
        participantDTO.setOwnerId(eventOwner.getId());
        participantDTO.setEventId(event.getId());
        participantDTO.setOwner(eventOwner);

        return participantDTO;
    }
}
