package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceTest {
    @Mock private StudentRepository studentRepository;
    @Mock private VerificationKeyRepository keyRepository;
    @Mock private CourseRepository courseRepository;
    private StudentService studentService;

    private Student expectedStudent;

    @Before
    public void setUp() {
        studentService = new StudentServiceImpl(studentRepository, keyRepository, courseRepository);

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
        Long id = 1L;
        expectedStudent.setId(id);
        SchoolSubject subjectOne = new SchoolSubject();
        subjectOne.setId(2L);
        SchoolSubject subjectTwo = new SchoolSubject();
        subjectTwo.setId(3L);
        StudyPlan planOne = new StudyPlan();
        planOne.setId(4L);
        StudyPlan planTwo = new StudyPlan();
        planTwo.setId(5L);
        Course courseOne = new Course(id, planOne.getId());
        Course courseTwo = new Course(id, planTwo.getId());
        List<Course> expectedCourses = Arrays.asList(courseOne, courseTwo);
        Teacher teacher = new Teacher();
        teacher.setId(8L);
        Stream.of(courseOne, courseTwo).forEach(teacher::addCourse);
        Mockito.when(studentRepository.findStudentById(id))
                .thenReturn(expectedStudent);
        Mockito.when(courseRepository.findAllById_StudentId(id))
                .thenReturn(expectedCourses)
                .thenReturn(Collections.emptyList());

        studentService.deleteStudentById(expectedStudent.getId());
        List<Course> actualCourses = courseRepository.findAllById_StudentId(id);

        verify(courseRepository, times(1)).delete(courseOne);
        verify(courseRepository, times(1)).delete(courseTwo);
        verify(studentRepository, times(1)).deleteById(id);
        assertThat(actualCourses)
                .isEmpty();
        assertThat(teacher)
                .hasFieldOrPropertyWithValue("courses", Collections.emptySet());
    }
}
