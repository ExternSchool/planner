package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchoolSubjectServiceImpl implements SchoolSubjectService {
    private SchoolSubjectRepository subjectRepository;

    public SchoolSubjectServiceImpl(final SchoolSubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolSubject findSubjectById(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }


    @Override
    @Transactional(readOnly = true)
    public SchoolSubject findSubjectByName(final String name) {
        return subjectRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolSubject> findAllByOrderByName() {
        return subjectRepository.findAllByOrderByName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolSubject> findAllById(final List<Long> indices) {
        return subjectRepository.findAllById(indices);
    }

    @Override
    public SchoolSubject saveOrUpdateSubject(SchoolSubject schoolSubject) {
        return subjectRepository.save(schoolSubject);
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        SchoolSubject subject = subjectRepository.findById(id).orElse(null);
        if (subject != null) {
            if (subject.getTeachers() != null) {
                for (Teacher teacher : subject.getTeachers()) {
                    teacher.removeSubject(subject);
                }
            }
            if (subject.getPlans() != null) {
                subject.getPlans().forEach(subject::removePlan);
            }

            subjectRepository.delete(subject);
        }
    }
}
