package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;

import java.util.List;

public interface SchoolSubjectService {

    SchoolSubject findSubjectById(Long id);

    SchoolSubject findSubjectByTitle(final String title);

    List<SchoolSubject> findAllByOrderByTitle();

    List<SchoolSubject> findAllById(List<Long> indices);

    SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject);

    void deleteSubjectById(Long id);
}
