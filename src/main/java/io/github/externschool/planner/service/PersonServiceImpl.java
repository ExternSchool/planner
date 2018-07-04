package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonSevice {

    private PersonRepository personRepository;

    public PersonServiceImpl(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person saveOrUpdatePerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person findPersonById(Long id) {
        return personRepository.getOne(id);
    }

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}
