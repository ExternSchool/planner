package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;

import java.util.List;

public interface SchoolSubjectService {

    SchoolSubject findSubjectById(Long id);

    SchoolSubject findSubjectByName(final String name);

    List<SchoolSubject> findAllByOrderByName();

    List<SchoolSubject> findAllById(List<Long> indices);

    SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject);

    void deleteSubject(Long id);
}
