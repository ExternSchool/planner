package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.profile.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PersonDTOToPerson implements Converter<PersonDTO, Person> {
    @Override
    public Person convert(PersonDTO personDTO) {
        Person person = new Person();
        person.setId(personDTO.getId());
        person.setUser(personDTO.getUser());
        person.setFirstName(personDTO.getFirstName());
        person.setPatronymicName(personDTO.getPatronymicName());
        person.setLastName(personDTO.getLastName());
        person.setPhoneNumber(personDTO.getPhoneNumber());
        person.setVerificationKey(personDTO.getVerificationKey());
        return person;
    }
}
