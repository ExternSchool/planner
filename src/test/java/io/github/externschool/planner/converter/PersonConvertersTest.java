package io.github.externschool.planner.converter;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.User;
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
    @Autowired ConversionService conversionService;

    private Person expectedPerson;
    private PersonDTO expectedDTO;

    @Before
    public void setup() {
        final Long id = 1L;
        final String firstName = "Philip";
        final String patronymicName = "K";
        final String lastName = "Dick";
        final String phoneNumber = "066-222-22-22";
        VerificationKey key = new VerificationKey();
        User user = new User("some@email.com", "pass");
        user.addVerificationKey(key);

        expectedPerson = new Person();
        expectedPerson.setId(id);
        expectedPerson.setFirstName(firstName);
        expectedPerson.setPatronymicName(patronymicName);
        expectedPerson.setLastName(lastName);
        expectedPerson.setPhoneNumber(phoneNumber);
        expectedPerson.addVerificationKey(key);

        expectedDTO = new PersonDTO();
        expectedDTO.setId(id);
        expectedDTO.setVerificationKey(key);
        expectedDTO.setEmail("some@email.com");
        expectedDTO.setFirstName(firstName);
        expectedDTO.setPatronymicName(patronymicName);
        expectedDTO.setLastName(lastName);
        expectedDTO.setPhoneNumber(phoneNumber);
    }

    @Test
    public void shouldReturnPersonDTO() {
        PersonDTO actualDTO = conversionService.convert(expectedPerson, PersonDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedDTO);
    }

    @Test
    public void shouldReturnEmptyEmailPersonDTO_whenPersonsKeyHasNoUser() {
        expectedPerson.getVerificationKey().getUser().removeVerificationKey();

        PersonDTO actualDTO = conversionService.convert(expectedPerson, PersonDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedDTO, "email")
                .hasFieldOrPropertyWithValue("email", "");
    }

    @Test
    public void shouldReturnExpectedPerson() {
        Person actualPerson = conversionService.convert(expectedDTO, Person.class);

        assertThat(actualPerson)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedPerson);
    }
}
