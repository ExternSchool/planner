package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.SchoolSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolSubjectRepository extends JpaRepository<SchoolSubject, Long> {

    SchoolSubject findByName(String name);

    List<SchoolSubject> findAllByOrderByNameAsc();

}
