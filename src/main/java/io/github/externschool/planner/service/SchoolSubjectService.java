package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;

import java.util.List;

public interface SchoolSubjectService {

    SchoolSubject findSubjectById(Long id);

    List<SchoolSubject> findAllByOrderByNameAsc();

    SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject);

    void deleteSubject(Long id);

}
