package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.VerificationKey;

import java.util.List;

public interface VerificationKeyService {

    VerificationKey findKeyByName(String name);

    List<VerificationKey> findAll();

    VerificationKey saveOrUpdateKey(VerificationKey verificationKey);

    void deleteById(String name);

}
