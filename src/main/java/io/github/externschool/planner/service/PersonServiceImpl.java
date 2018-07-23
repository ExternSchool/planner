package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

   private PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person saveOrUpdatePerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Optional<Person> findPersonById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public List<Person> findAllByOrderByNameAsc() {
        return personRepository.findAllByOrderByLastNameAsc();
    }

    @Override
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}
