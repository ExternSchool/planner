package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;

import java.util.List;


public interface SchoolSubjectService {
    SchoolSubject findSubjectById(Long id);

    SchoolSubject findSubjectByName(String name);

    List<SchoolSubject> findAll();

    SchoolSubject saveOrUpdateSubject(SchoolSubject subject);

    void deleteSubjectById(Long id);
}