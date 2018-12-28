package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final VerificationKeyRepository keyRepository;

    @Autowired
    public PersonServiceImpl(final PersonRepository personRepository,
                             final UserRepository userRepository,
                             final VerificationKeyRepository keyRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.keyRepository = keyRepository;
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
    public Person findPersonByFullNameAndPhoneNumber(final Person person) {
        return personRepository.findPersonByFirstNameAndPatronymicNameAndLastName(
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Person> findAllByOrderByName() {
        return personRepository.findAllByOrderByLastName();
    }

    @Override
    public void deletePerson(Person person) {
        if (person != null && findPersonById(person.getId()) != null) {
            Optional.ofNullable(person.getVerificationKey()).ifPresent(key -> {
                Optional.ofNullable(key.getUser()).ifPresent(user -> {
                    user.removeVerificationKey();
                    userRepository.save(user);
                });
                person.removeVerificationKey();
                keyRepository.delete(key);
            });
            personRepository.delete(person);
        }
    }
}
