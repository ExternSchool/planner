package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class PersonDTOToPerson implements Converter<PersonDTO, Person> {
    @Autowired private VerificationKeyService keyService;

    @Override
    public Person convert(PersonDTO personDTO) {
        Person person = new Person();
        person.setId(personDTO.getId());
        person.addVerificationKey(keyService.findKeyByValue(personDTO.getVerificationKeyValue()));
        person.setFirstName(personDTO.getFirstName());
        person.setPatronymicName(personDTO.getPatronymicName());
        person.setLastName(personDTO.getLastName());
        person.setPhoneNumber(personDTO.getPhoneNumber());

        return person;
    }
}
