package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.VerificationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationKeyRepository extends JpaRepository<VerificationKey, Long> {

    VerificationKey findByValue(String value);
}
