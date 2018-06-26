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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SchoolSubjectServiceTest {

    @Mock
    private SchoolSubjectRepository subjectRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private SchoolSubjectServiceImpl schoolSubjectService;

    private Teacher teacher;
    private SchoolSubject schoolSubject1;
    private SchoolSubject schoolSubject2;
    private List<SchoolSubject> subjects = new ArrayList<>();

    @Before
    public void setup(){
        teacher = new Teacher();
        teacher.setOfficer("Math");

        schoolSubject1 = new SchoolSubject();
        schoolSubject1.setId(1L);
        schoolSubject1.setName("math");

        schoolSubject2 = new SchoolSubject();
        schoolSubject2.setId(2L);
        schoolSubject2.setName("history");


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
    public void shouldReturnSubjectList(){
        Mockito.when(subjectRepository.findAll())
                .thenReturn(subjects);
    }

    @Test
    public void shouldDeleteSchoolSubject(){
        //code here
    }
}
