package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.profile.Person;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonToPersonDTO implements Converter<Person, PersonDTO> {
    @Override
    public PersonDTO convert(Person person) {
        PersonDTO personDTO = new PersonDTO();
        BeanUtils.copyProperties(person, personDTO);
        personDTO.setEmail("");
        Optional.ofNullable(person.getVerificationKey())
                .ifPresent(key -> Optional.ofNullable(key.getUser())
                        .ifPresent(user -> personDTO.setEmail(user.getEmail())));

        return personDTO;
    }
}
