package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
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
    public List<Person> findAllByOrderByName() {
        return personRepository.findAllByOrderByLastName();
    }

    @Transactional
    @Override
    public void deletePerson(Person person) {
        if (person != null) {
            VerificationKey key = person.getVerificationKey();
            User user = key.getUser();
            if (user != null) {
                user.removeVerificationKey();
                userRepository.save(user);
            }
            person.removeVerificationKey();
            keyRepository.delete(key);
            personRepository.delete(person);
        }
    }
}
