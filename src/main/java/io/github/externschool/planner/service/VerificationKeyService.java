package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.VerificationKey;

import java.util.List;

public interface VerificationKeyService {
    void deleteById(Long id);

    VerificationKey findKeyById(Long id);

    VerificationKey findKeyByValue(String value);

    VerificationKey saveOrUpdateKey(VerificationKey verificationKey);

    List<VerificationKey> findAll();

    PersonDTO setNewKeyToDTO(PersonDTO personDTO);
}
