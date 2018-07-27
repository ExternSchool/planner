package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Student expectedStudent;

    @Before
    public void setUp() {
        expectedStudent = new Student();
        expectedStudent.setLastName("LastName");

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnStudent_whenFindStudentById() {
        Mockito.when(studentRepository.findStudentById(expectedStudent.getId()))
                .thenReturn(expectedStudent);

        Student actualStudent = studentService.findStudentById(expectedStudent.getId());

        assertThat(actualStudent)
                .isNotNull()
                .isEqualTo(expectedStudent)
                .isEqualToComparingFieldByField(expectedStudent);
    }

    @Test
    public void shouldReturnList_whenFindAllStudents() {
        List<Student> expectedList = new ArrayList<>();
        expectedList.add(expectedStudent);

        Mockito.when(studentRepository.findAll())
                .thenReturn(expectedList);

        List<Student> actualList = studentService.findAllStudents();

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(ArrayList.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
    }

    @Test
    public void shouldReturnSortedList_whenFindAllByOrderByLastNameAsc() {
        List<Student> expectedList = new ArrayList<>();
        expectedList.add(expectedStudent);

        Mockito.when(studentRepository.findAllByOrderByLastName())
                .thenReturn(expectedList);

        List<Student> actualList = studentService.findAllByOrderByLastName();

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(ArrayList.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
    }


    @Test
    public void shouldReturnStudent_whenSaveOrUpdateStudent() {
        Mockito.when(studentRepository.save(expectedStudent))
                .thenReturn(expectedStudent);

        Student actualStudent = studentService.saveOrUpdateStudent(expectedStudent);

        assertThat(actualStudent)
                .isNotNull()
                .isEqualTo(expectedStudent)
                .isEqualToComparingFieldByField(expectedStudent);
    }

    @Test
    public void noneException_whenDeleteStudent() {

        studentService.deleteStudent(expectedStudent.getId());
    }

}
