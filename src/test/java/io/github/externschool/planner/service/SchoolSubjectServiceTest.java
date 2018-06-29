package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchoolSubjectServiceTest {

    @MockBean
    private SchoolSubjectRepository subjectRepository;

    @MockBean
    private TeacherServiceImpl teacherService;

    @Autowired
    private SchoolSubjectServiceImpl schoolSubjectService;

    private Teacher mathAndHistoryTeacher;
    private Teacher mathOnlyTeacher;
    private Teacher anotherMathTeacher;

    private SchoolSubject schoolSubject1;
    private SchoolSubject schoolSubject2;

    List<Teacher> teachers = new ArrayList<>();
    List<SchoolSubject> subjects = new ArrayList<>();

    @Before
    public void setup(){

        mathAndHistoryTeacher = new Teacher();
        mathOnlyTeacher = new Teacher();
        anotherMathTeacher = new Teacher();

        schoolSubject1 = new SchoolSubject();
        schoolSubject1.setId(1L);
        schoolSubject1.setName("math");

        schoolSubject2 = new SchoolSubject();
        schoolSubject2.setId(2L);
        schoolSubject2.setName("history");

        mathAndHistoryTeacher.addSubject(schoolSubject1);
        mathAndHistoryTeacher.addSubject(schoolSubject2);

        mathOnlyTeacher.addSubject(schoolSubject1);

        anotherMathTeacher.addSubject(schoolSubject1);

        teachers.add(mathAndHistoryTeacher);
        teachers.add(mathOnlyTeacher);
        teachers.add(anotherMathTeacher);

        subjects.add(schoolSubject1);
        subjects.add(schoolSubject2);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnSubjectsById(){

        Mockito.when(subjectRepository.getOne(schoolSubject1.getId()))
                .thenReturn(schoolSubject1);

        SchoolSubject actualSubject = schoolSubjectService.findSubjectById(schoolSubject1.getId());

        assertThat(actualSubject.getName()).isEqualTo(schoolSubject1.getName());

    }

    @Test
    public void shouldDeleteSubject_ById(){

        Mockito.when(teacherService.findAllTeachers())
                .thenReturn(teachers);

        Mockito.when(subjectRepository.findAll())
                .thenReturn(subjects);

        List<Teacher> expectedTeacher = new ArrayList<>();
        expectedTeacher.add(mathOnlyTeacher);
        expectedTeacher.add(anotherMathTeacher);

        List<SchoolSubject> expectedSubjects = new ArrayList<>();
        expectedSubjects.add(schoolSubject1);

        schoolSubjectService.deleteSubject(schoolSubject2.getId());

        for (Teacher teacher: expectedTeacher) {

            assertThat(teacher.getSubjects().contains(schoolSubject2)).isFalse();
        }

        assertThat(expectedSubjects.contains(schoolSubject2)).isFalse();

    }

    @After
    public void tearDown(){
        subjectRepository.deleteAll();
    }
}
