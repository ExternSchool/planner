package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VerificationKeyServiceImpl implements VerificationKeyService {
    private VerificationKeyRepository repository;

    public VerificationKeyServiceImpl(VerificationKeyRepository verificationKeyRepository) {
        this.repository = verificationKeyRepository;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        VerificationKey key = repository.findById(id).orElse(null);
        if (key != null) {
            repository.delete(key);
        }
    }

    @Override
    public VerificationKey findKeyById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public VerificationKey findKeyByValue(final String value) {
        return repository.findByValue(value);
    }

    @Override
    public VerificationKey saveOrUpdateKey(VerificationKey verificationKey) {
        return repository.save(verificationKey);
    }

    @Override
    public List<VerificationKey> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public PersonDTO setNewKeyToDTO(PersonDTO personDTO) {
        VerificationKey newKey = repository.save(new VerificationKey());
        VerificationKey oldKey = personDTO.getVerificationKey();
        if (oldKey != null) {
            User oldUser = oldKey.getUser();
            Person oldPerson = oldKey.getPerson();
            if (oldUser != null) {
                oldUser.removeVerificationKey();
            }
            if (oldPerson != null) {
                oldPerson.removeVerificationKey();
                oldPerson.addVerificationKey(newKey);
            }
            deleteById(oldKey.getId());
        }
        personDTO.setVerificationKey(newKey);

        return personDTO;
    }
}
