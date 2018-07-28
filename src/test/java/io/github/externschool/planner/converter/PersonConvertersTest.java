package io.github.externschool.planner.converter;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlannerApplication.class)
public class PersonConvertersTest {

    @Autowired
    ConversionService conversionService;
    private Person expectedPerson;
    private PersonDTO personDTO;

    @Before
    public void setup() {
        final Long id = 1L;
        final String firstName = "Philip";
        final String patronymicName = "K";
        final String lastName = "Dick";
        final String phoneNumber = "066-222-22-22";
        final VerificationKey verificationKey = new VerificationKey();

        expectedPerson = new Person();
        expectedPerson.setId(id);
        expectedPerson.setFirstName(firstName);
        expectedPerson.setPatronymicName(patronymicName);
        expectedPerson.setLastName(lastName);
        expectedPerson.setPhoneNumber(phoneNumber);
        expectedPerson.addVerificationKey(verificationKey);

        personDTO = new PersonDTO();
        personDTO.setId(id);
        personDTO.setFirstName(firstName);
        personDTO.setPatronymicName(patronymicName);
        personDTO.setLastName(lastName);
        personDTO.setPhoneNumber(phoneNumber);
        personDTO.setVerificationKey(verificationKey);
    }

    @Test
    public void shouldReturnPersonDTO() {
        PersonDTO actualDTO = conversionService.convert(expectedPerson, PersonDTO.class);

        assertThat(actualDTO).isEqualToComparingFieldByField(personDTO);
    }

    @Test
    public void shouldReturnExpectedPerson() {
        Person actualPerson = conversionService.convert(personDTO, Person.class);

        assertThat(actualPerson).isNotNull()
                .isEqualTo(actualPerson)
                .isEqualToComparingFieldByFieldRecursively(expectedPerson);
    }
}
