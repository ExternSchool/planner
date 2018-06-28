package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SchoolSubjectServiceTest {

    @Mock
    private SchoolSubjectRepository subjectRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private SchoolSubjectServiceImpl schoolSubjectService;

    private Teacher mathAndHistoryTeacher;
    private Teacher mathOnlyTeacher;
    private SchoolSubject schoolSubject1;
    private SchoolSubject schoolSubject2;

    @Before
    public void setup(){
        mathAndHistoryTeacher = new Teacher();
       // mathAndHistoryTeacher.setOfficer("MathAndHistory");

        mathOnlyTeacher = new Teacher();
        //mathOnlyTeacher.setOfficer("Math");


        schoolSubject1 = new SchoolSubject();
        schoolSubject1.setId(1L);
        schoolSubject1.setName("math");

        schoolSubject2 = new SchoolSubject();
        schoolSubject2.setId(2L);
        schoolSubject2.setName("history");

        mathAndHistoryTeacher.addSubject(schoolSubject1);
        mathAndHistoryTeacher.addSubject(schoolSubject2);

        mathOnlyTeacher.addSubject(schoolSubject1);


        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void shouldReturnSubjectsById(){

        Mockito.when(subjectRepository.getOne(schoolSubject1.getId()))
                .thenReturn(schoolSubject1);

        SchoolSubject actualSubject1 = schoolSubjectService.findSubjectById(schoolSubject1.getId());

        assertThat(actualSubject1).isEqualTo(schoolSubject1);

    }

    @Test
    public void shouldDeleteSchoolSubjectFromTeacher(){

        Mockito.when(teacherRepository.findById(mathAndHistoryTeacher.getId()))
                .thenReturn(java.util.Optional.ofNullable(mathAndHistoryTeacher));

        Optional<Teacher> actualTeacher = teacherRepository.findById(mathAndHistoryTeacher.getId());

        System.out.println(actualTeacher.get());

        schoolSubjectService.deleteSubjectFromTeacher(actualTeacher, schoolSubject2);

        System.out.println(actualTeacher.get());

        assertThat(actualTeacher.get().getSubjects()).isEqualTo(mathOnlyTeacher.getSubjects());

    }
}
