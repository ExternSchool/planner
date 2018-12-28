package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.exceptions.KeyNotValidException;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VerificationKeyServiceImpl implements VerificationKeyService {
    private VerificationKeyRepository repository;

    public VerificationKeyServiceImpl(VerificationKeyRepository verificationKeyRepository) {
        this.repository = verificationKeyRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public VerificationKey findKeyById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public VerificationKey findKeyByValue(final String value) {
        return repository.findByValue(value);
    }

    @Transactional(readOnly = true)
    @Override
    public List<VerificationKey> findAll() {
        return repository.findAll();
    }

    @Override
    public VerificationKey saveOrUpdateKey(VerificationKey verificationKey) throws KeyNotValidException {
        if (verificationKey == null) {
            throw new KeyNotValidException("An attempt to save NULL key");
        }
        return repository.save(verificationKey);
    }

    @Override
    public void deleteById(Long id) {
        repository.findById(id).ifPresent(repository::delete);
    }

    @Override
    public PersonDTO setNewKeyToDTO(PersonDTO personDTO) {
        VerificationKey newKey = repository.save(new VerificationKey());
        Optional.ofNullable(personDTO.getVerificationKey()).ifPresent(key -> {
            Optional.ofNullable(key.getUser()).ifPresent(User::removeVerificationKey);
            Optional.ofNullable(key.getPerson()).ifPresent(person -> {
                person.removeVerificationKey();
                person.addVerificationKey(newKey);
            });
            deleteById(key.getId());
        });
        personDTO.setVerificationKey(newKey);

        return personDTO;
    }
}
