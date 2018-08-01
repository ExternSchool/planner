package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    @Override
    public Person findPersonById(Long id) {
        return personRepository.findPersonById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Person> findAllByOrderByName() {
        return personRepository.findAllByOrderByLastName();
    }

    @Override
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}
