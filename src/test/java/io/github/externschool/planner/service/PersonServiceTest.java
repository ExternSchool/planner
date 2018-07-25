package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {

    @MockBean
    private PersonRepository personRepository;

    @Autowired
    private PersonServiceImpl personService;

    private List<Person> personList;
    private Person firstPerson;
    private Person secondPerson;

    @Before
    public void setup(){
        personList = new ArrayList<>();

        firstPerson = new Person();
        firstPerson.setFirstName("Dmytro");

        secondPerson = new Person();
        secondPerson.setFirstName("Vasia");

        personList.add(firstPerson);
        personList.add(secondPerson);
    }

    @Test
    public void shouldSaveNewPerson_whenSaveOrUpdate(){
        Mockito.when(personRepository.save(firstPerson))
            .thenReturn(firstPerson);

        Person actualPerson = personService.saveOrUpdatePerson(firstPerson);

        assertThat(actualPerson).isNotNull()
                .isEqualTo(firstPerson)
                .isEqualToComparingFieldByField(firstPerson);
    }

    @Test
    public void shouldReturnAllPerson_WhenFindAll(){
        Mockito.when(personRepository.findAllByOrderByLastNameAsc())
                .thenReturn(personList);
        List<Person> expectedPersonList = personService.findAllByOrderByNameAsc();

        assertThat(expectedPersonList).isNotNull();
        assertThat(expectedPersonList.contains(firstPerson)).isTrue();
        assertThat(expectedPersonList.contains(secondPerson)).isTrue();
    }

    @Test
    public void shouldReturnPerson_whenFindById(){
        Mockito.when(personRepository.findPersonById(1L))
                .thenReturn(firstPerson);
        Person foundPerson = personService.findPersonById(1L);

        assertThat(foundPerson).isEqualTo(firstPerson);
    }

    @Test
    public void shouldDeletePerson_whenDeleteById() {
        Mockito.when(personRepository.findById(firstPerson.getId()))
                .thenReturn(null);

        personService.deletePerson(firstPerson.getId());
    }
}
