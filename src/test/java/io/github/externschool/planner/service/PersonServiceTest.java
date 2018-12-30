package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PersonRepository personRepository;
    @Mock private VerificationKeyRepository keyRepository;
    private PersonService personService;

    private List<Person> personList;
    private Person firstPerson;
    private Person secondPerson;
    private User user;

    @Before
    public void setup(){
        personService = new PersonServiceImpl(personRepository,userRepository, keyRepository);

        VerificationKey key = new VerificationKey();
        firstPerson = new Person();
        firstPerson.setLastName("A");
        firstPerson.addVerificationKey(key);
        user = new User();
        user.setEmail("email");
        user.setPassword("pass");
        user.addVerificationKey(key);

        secondPerson = new Person();
        secondPerson.setLastName("B");

        personList = new ArrayList<>();
        personList.add(firstPerson);
        personList.add(secondPerson);
    }

    @Test
    public void shouldSaveNewPerson_whenSaveOrUpdate(){
        Mockito.when(personRepository.save(firstPerson))
            .thenReturn(firstPerson);

        Person actualPerson = personService.saveOrUpdatePerson(firstPerson);

        assertThat(actualPerson)
                .isNotNull()
                .isEqualTo(firstPerson)
                .isEqualToComparingFieldByField(firstPerson);
    }

    @Test
    public void shouldSaveNewPersonAndVerificationKey_whenSaveOrUpdate(){
        Mockito.when(personRepository.save(firstPerson))
                .thenReturn(firstPerson);

        Person actualPerson = personService.saveOrUpdatePerson(firstPerson);

        assertThat(actualPerson).hasFieldOrProperty("verificationKey");
        assertThat(actualPerson.getVerificationKey()).isEqualTo(firstPerson.getVerificationKey());

    }

    @Test
    public void shouldReturnAllPersonsSortedByLastName_WhenFindAll(){
        Mockito.when(personRepository.findAllByOrderByLastName())
                .thenReturn(personList);
        List<Person> expectedPersonList = personService.findAllByOrderByName();

        assertThat(expectedPersonList)
                .isNotNull()
                .contains(firstPerson)
                .contains(secondPerson)
                .containsSequence(Arrays.asList(firstPerson, secondPerson));
    }

    @Test
    public void shouldReturnPerson_whenFindById(){
        Mockito.when(personRepository.findPersonById(1L))
                .thenReturn(firstPerson);

        Person foundPerson = personService.findPersonById(1L);

        assertThat(foundPerson)
                .isEqualTo(firstPerson);
    }


    @Test
    public void shouldReturnPerson_whenFindByFullName(){
        Mockito.when(personRepository.findPersonByFirstNameAndPatronymicNameAndLastName(
                firstPerson.getFirstName(),
                firstPerson.getPatronymicName(),
                firstPerson.getLastName()))
                .thenReturn(firstPerson);

        Person foundPerson = personService.findPersonByFullNameAndPhoneNumber(firstPerson);

        assertThat(foundPerson)
                .isEqualTo(firstPerson);
    }

    @Test
    public void shouldDeletePerson_whenDelete() {
        Mockito.when(personRepository.findPersonById(firstPerson.getId()))
                .thenReturn(firstPerson)
                .thenReturn(null);
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(null);

        personService.deletePerson(firstPerson);

        assertThat(personService.findPersonById(firstPerson.getId()))
                .isNull();
        assertThat(user.getVerificationKey())
                .isNull();
    }
}
