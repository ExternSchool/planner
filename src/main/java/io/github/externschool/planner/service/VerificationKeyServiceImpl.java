package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.exceptions.KeyNotValidException;
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

    @Transactional
    @Override
    public void deleteById(Long id) {
        repository.findById(id).ifPresent(repository::delete);
    }

    @Override
    public VerificationKey findKeyById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public VerificationKey findKeyByValue(final String value) {
        return repository.findByValue(value);
    }

    @Transactional
    @Override
    public VerificationKey saveOrUpdateKey(VerificationKey verificationKey) throws KeyNotValidException {
        if (verificationKey == null) {
            throw new KeyNotValidException("An attempt to save NULL key");
        }
        return repository.save(verificationKey);
    }

    @Override
    public List<VerificationKey> findAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public PersonDTO setNewKeyToDTO(PersonDTO personDTO) {
        VerificationKey newKey = repository.save(new VerificationKey());
        VerificationKey oldKey = findKeyByValue(personDTO.getVerificationKeyValue());
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
        personDTO.setVerificationKeyValue(newKey.getValue());

        return personDTO;
    }
}
