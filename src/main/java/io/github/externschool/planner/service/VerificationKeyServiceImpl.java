package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificationKeyServiceImpl implements VerificationKeyService {

    private VerificationKeyRepository verificationKeyRepository;

    public VerificationKeyServiceImpl(VerificationKeyRepository verificationKeyRepository) {
        this.verificationKeyRepository = verificationKeyRepository;
    }

    @Override
    public VerificationKey findKeyById(Long id) {
        return verificationKeyRepository.getById(id);
    }

    @Override
    public List<VerificationKey> findAll() {
        return verificationKeyRepository.findAll();
    }

    @Override
    public VerificationKey saveOrUpdateKey(VerificationKey verificationKey) {
        return verificationKeyRepository.save(verificationKey);
    }

    @Override
    public void deleteById(String id) {
        verificationKeyRepository.deleteById(id);
    }
}