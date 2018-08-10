package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceTest {
    @Mock private StudentRepository studentRepository;
    @Mock private VerificationKeyRepository keyRepository;
    private StudentService studentService;

    private Student expectedStudent;

    @Before
    public void setUp() {
        studentService = new StudentServiceImpl(studentRepository, keyRepository);

        expectedStudent = new Student();
        expectedStudent.setLastName("LastName");
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
    public void shouldReturnSortedList_whenFindAllByOrderByLastName() {
        Student anotherStudent = new Student();
        anotherStudent.setLastName("ZeroName");
        List<Student> expectedList = Arrays.asList(expectedStudent, anotherStudent);

        Mockito.when(studentRepository.findAllByOrderByLastName())
                .thenReturn(expectedList);

        List<Student> actualList = studentService.findAllByOrderByLastName();

        assertThat(actualList)
                .isNotEmpty()
                .containsSequence(expectedList);
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
    public void shouldRemoveKeyFromUserAndDeleteKey_whenDeleteTeacher() {
        User user = new User();
        VerificationKey key =  new VerificationKey();
        user.addVerificationKey(key);
        expectedStudent.setId(1L);
        expectedStudent.addVerificationKey(key);
        Mockito.when(studentRepository.findStudentById(expectedStudent.getId()))
                .thenReturn(expectedStudent);

        studentService.deleteStudentById(expectedStudent.getId());

        verify(studentRepository, times(1)).deleteById(expectedStudent.getId());
        verify(keyRepository, times(1)).delete(key);
        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("verificationKey", null);
    }

    @Test
    public void shouldRemoveStudentsCourses_whenDeleteStudent() {
        //TODO Complete this
//        expectedTeacher.setId(1L);
//        SchoolSubject subjectOne = new SchoolSubject();
//        subjectOne.setId(2L);
//        SchoolSubject subjectTwo = new SchoolSubject();
//        subjectTwo.setId(3L);
//        Stream.of(subjectOne, subjectTwo).forEach(subject -> expectedTeacher.addSubject(subject));
//        Course courseOne = new Course(4L, 5L);
//        Course courseTwo = new Course(6L, 7L);
//        Stream.of(courseOne, courseTwo).forEach(course -> expectedTeacher.addCourse(course));
//        Mockito.when(teacherRepository.findTeacherById(expectedTeacher.getId()))
//                .thenReturn(expectedTeacher);
//
//        Stream.of(subjectOne, subjectTwo).forEach(subject ->
//                assertThat(subject.getTeachers())
//                        .contains(expectedTeacher));
//        Stream.of(courseOne, courseTwo).forEach(course ->
//                assertThat(course.getTeacher())
//                        .isEqualTo(expectedTeacher));
//
//        teacherService.deleteTeacherById(expectedTeacher.getId());
//
//        Stream.of(subjectOne, subjectTwo).forEach(subject ->
//                assertThat(subject.getTeachers())
//                        .isEmpty());
//        Stream.of(courseOne, courseTwo).forEach(course ->
//                assertThat(course.getTeacher())
//                        .isNull());
    }
}
