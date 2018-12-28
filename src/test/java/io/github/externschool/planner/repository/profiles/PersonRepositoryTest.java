package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryTest {
    @Autowired private PersonRepository personRepository;
    @Autowired TestEntityManager entityManager;

    private List<Person> expectedPersons;

    @Before
    public void setUp() {
        expectedPersons = new ArrayList<>();
        List<String> names = Arrays.asList("A", "B", "C");
        for (String name : names) {
            Person person = new Person();
            person.setLastName(name);
            entityManager.persist(person);
            expectedPersons.add(person);
        }
    }

    @Test
    public void shouldReturnPerson_whenFindByAllNames(){
        Person person = new Person();
        person.setLastName("Last");
        person.setFirstName("First");
        person.setPatronymicName("Pat");
        person.setPhoneNumber("123-45-67");
        entityManager.persist(person);

        Person actualPerson = personRepository.findPersonByFirstNameAndPatronymicNameAndLastName(
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName());

        assertThat(actualPerson)
                .isNotNull()
                .isEqualTo(person);
    }


    @Test
    public void shouldReturnOrderedListOfThreePersons_whenFindAllByOrderByLastName(){
        List<Person> actualPersons = personRepository.findAll();

        assertThat(actualPersons)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(expectedPersons);
    }

    @Test
    public void shouldReturnPerson_whenFindPersonById() {
        Person actualPerson = personRepository.findPersonById(expectedPersons.get(0).getId());

        assertThat(actualPerson)
                .isNotNull()
                .isEqualTo(expectedPersons.get(0));
    }
}
