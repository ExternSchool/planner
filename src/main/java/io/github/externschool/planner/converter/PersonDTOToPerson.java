package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonDTOToPerson implements Converter<PersonDTO, Person> {
    @Override
    public Person convert(PersonDTO personDTO) {
        Person person = new Person();
        BeanUtils.copyProperties(personDTO, person, "verificationKey", "email");
        Optional.ofNullable(personDTO.getVerificationKey()).ifPresent(person::addVerificationKey);

        return person;
    }
}
