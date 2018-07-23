package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.VerificationKey;

import java.util.List;

public interface VerificationKeyService {
    VerificationKey findKeyById(Long id);
    List<VerificationKey> findAll();
    VerificationKey saveOrUpdateKey(VerificationKey verificationKey);
    void deleteById(Long id);
    PersonDTO setNewKeyToDTO(PersonDTO personDTO);
}
