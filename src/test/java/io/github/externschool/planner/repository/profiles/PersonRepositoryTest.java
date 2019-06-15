package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Person;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PersonRepositoryTest {
    @Autowired private PersonRepository personRepository;
    @Autowired TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<Person> expectedPersons;

    @Before
    public void setUp() {
        expectedPersons = new ArrayList<>();
        List<String> names = Arrays.asList("Aaaa", "Bbbb", "Cccc");
        for (String name : names) {
            Person person = new Person();
            person.setLastName(name);
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
        int initialCount = (int)personRepository.count();
        personRepository.saveAll(expectedPersons);

        List<Person> actualPersons = personRepository.findAllByOrderByLastName();

        assertThat(actualPersons)
                .isNotNull()
                .hasSize(initialCount + 3)
                .containsSubsequence(expectedPersons);
    }

    @Test
    public void shouldReturnPerson_whenFindPersonById() {
        personRepository.saveAll(expectedPersons);

        Person actualPerson = personRepository.findPersonById(expectedPersons.get(0).getId());

        assertThat(actualPerson)
                .isNotNull()
                .isEqualTo(expectedPersons.get(0));
    }
}
