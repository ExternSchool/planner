package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolSubjectServiceImpl implements SchoolSubjectService {
@Autowired
    private SchoolSubjectService schoolSubjectService;




    @Override
    public SchoolSubject findSubjectById(Long id) {
        return schoolSubjectService.findSubjectById(id);
    }

    @Override
    public SchoolSubject findSubjectByName(String name) {
        return null;
    }

    @Override
    public List<SchoolSubject> findAll() {
        return schoolSubjectService.findAll() ;
    }

    @Override
    public SchoolSubject saveOrUpdateSubject(SchoolSubject subject) {
        return schoolSubjectService.saveOrUpdateSubject(subject);
    }

    @Override
    public void deleteSubjectById(Long id) {
        schoolSubjectService.deleteSubjectById(id);

    }
}
