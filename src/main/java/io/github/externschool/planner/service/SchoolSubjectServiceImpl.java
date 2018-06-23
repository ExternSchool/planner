package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolSubjectServiceImpl implements SchoolSubjectService {

    private SchoolSubjectRepository subjectRepository;

    @Override
    public SchoolSubject findSubjectById(Long id) {
        return subjectRepository.getOne(id);
    }

    @Override
    public List<SchoolSubject> findAll() {
        return subjectRepository.findAll();
    }

    @Override
    public SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject) {
        return subjectRepository.save(schoolSubject);
    }

    @Override
    public void deleteSubjectById(Long id) {
        subjectRepository.deleteById(id);
    }
}
