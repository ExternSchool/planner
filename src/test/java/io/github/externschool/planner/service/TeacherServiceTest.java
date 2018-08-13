package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeacherServiceTest {
    @Mock private TeacherRepository teacherRepository;
    @Mock private VerificationKeyRepository keyRepository;
    private TeacherService teacherService;

    @Rule public ExpectedException thrown = ExpectedException.none();

    private Teacher expectedTeacher;

    @Before
    public void setUp() {
        teacherService = new TeacherServiceImpl(teacherRepository, keyRepository);

        expectedTeacher = new Teacher();
        expectedTeacher.setLastName("LastName");
    }

    @Test
    public void shouldReturnTeacher_whenFindTeacherById() {
        Mockito.when(teacherRepository.findTeacherById(expectedTeacher.getId()))
                .thenReturn(expectedTeacher);

        Teacher actualTeacher = teacherService.findTeacherById(expectedTeacher.getId());

        assertThat(actualTeacher)
                .isNotNull()
                .isEqualTo(expectedTeacher)
                .isEqualToComparingFieldByField(expectedTeacher);
    }

    @Test
    public void shouldReturnList_whenFindAllTeachers() {
        List<Teacher> expectedList = Collections.singletonList(expectedTeacher);

        Mockito.when(teacherRepository.findAll())
                .thenReturn(expectedList);

        List<Teacher> actualList = teacherService.findAllTeachers();

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(List.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
    }

    @Test
    public void shouldReturnList_whenFindAllBySubject() {
        List<Teacher> expectedList = Collections.singletonList(expectedTeacher);
        SchoolSubject subject = new SchoolSubject();

        Mockito.when(teacherRepository.findAllBySubjectsContains(subject))
                .thenReturn(expectedList);

        List<Teacher> actualList = teacherService.findAllBySubject(subject);

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(List.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
    }

    @Test
    public void shouldReturnSortedList_whenFindAllByOrderByLastNameAsc() {
        Teacher anotherTeacher =  new Teacher();
        anotherTeacher.setLastName("ZetName");
        List<Teacher> expectedList = Arrays.asList(expectedTeacher, anotherTeacher);

        Mockito.when(teacherRepository.findAllByOrderByLastName())
                .thenReturn(expectedList);

        List<Teacher> actualList = teacherService.findAllByOrderByLastName();

        assertThat(actualList)
                .isNotEmpty()
                .containsSequence(expectedList);
    }

    @Test
    public void shouldReturnTeacher_whenSaveOrUpdateTeacher() {
        Mockito.when(teacherRepository.save(expectedTeacher))
                .thenReturn(expectedTeacher);

        Teacher actualTeacher = teacherService.saveOrUpdateTeacher(expectedTeacher);

        assertThat(actualTeacher)
                .isNotNull()
                .isEqualTo(expectedTeacher)
                .isEqualToComparingFieldByField(expectedTeacher);
    }

    @Test
    public void shouldRemoveKeyFromUserAndDeleteKey_whenDeleteTeacher() {
        User user = new User();
        VerificationKey key =  new VerificationKey();
        user.addVerificationKey(key);
        expectedTeacher.setId(1L);
        expectedTeacher.addVerificationKey(key);
        Mockito.when(teacherRepository.findTeacherById(expectedTeacher.getId()))
                .thenReturn(expectedTeacher);

        teacherService.deleteTeacherById(expectedTeacher.getId());

        verify(teacherRepository, times(1)).deleteById(expectedTeacher.getId());
        verify(keyRepository, times(1)).delete(key);
        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("verificationKey", null);
    }

    @Test
    public void shouldRemoveTeacherFromSubjectsAndCourses_whenDeleteTeacher() {
        expectedTeacher.setId(1L);
        SchoolSubject subjectOne = new SchoolSubject();
        subjectOne.setId(2L);
        SchoolSubject subjectTwo = new SchoolSubject();
        subjectTwo.setId(3L);
        Stream.of(subjectOne, subjectTwo).forEach(subject -> expectedTeacher.addSubject(subject));
        Course courseOne = new Course(4L, 5L);
        Course courseTwo = new Course(6L, 7L);
        Stream.of(courseOne, courseTwo).forEach(course -> expectedTeacher.addCourse(course));
        Mockito.when(teacherRepository.findTeacherById(expectedTeacher.getId()))
                .thenReturn(expectedTeacher);

        Stream.of(subjectOne, subjectTwo).forEach(subject ->
                assertThat(subject.getTeachers())
                        .contains(expectedTeacher));
        Stream.of(courseOne, courseTwo).forEach(course ->
                assertThat(course.getTeacher())
                        .isEqualTo(expectedTeacher));

        teacherService.deleteTeacherById(expectedTeacher.getId());

        Stream.of(subjectOne, subjectTwo).forEach(subject ->
                assertThat(subject.getTeachers())
                        .isEmpty());
        Stream.of(courseOne, courseTwo).forEach(course ->
                assertThat(course.getTeacher())
                        .isNull());
    }
}
