package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Guest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class GuestRepositoryTest {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void shouldReturnListOfPersons(){

        Guest person1 = new Guest();
        person1.setFirstName("Dmytro");
        person1.setLastName("Manzhula");

        Guest person2 = new Guest();
        person2.setFirstName("Vasia");
        person2.setLastName("Pupkin");

        entityManager.persist(person1);
        entityManager.persist(person2);


        List<Guest> personList = this.guestRepository.findAll();

        assertThat(personList).isNotNull()
                .hasSize(2).containsSubsequence(person1, person2);

    }
}
