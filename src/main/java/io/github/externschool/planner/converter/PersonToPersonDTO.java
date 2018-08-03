package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.profile.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PersonToPersonDTO implements Converter<Person, PersonDTO> {

    @Override
    public PersonDTO convert(Person person) {
        return new PersonDTO(
                person.getId(),
                person.getVerificationKey().getValue(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber());
    }
}
