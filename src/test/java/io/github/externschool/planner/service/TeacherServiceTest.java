package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Teacher expectedTeacher;

    @Before
    public void setUp() {
        expectedTeacher = new Teacher();
        expectedTeacher.setLastName("LastName");

        MockitoAnnotations.initMocks(this);
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
        List<Teacher> expectedList = new ArrayList<>();
        expectedList.add(expectedTeacher);

        Mockito.when(teacherRepository.findAll())
                .thenReturn(expectedList);

        List<Teacher> actualList = teacherService.findAllTeachers();

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(ArrayList.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
    }

    @Test
    public void shouldReturnSortedList_whenFindAllSortByLastNameAndFirstName() {
        List<Teacher> expectedList = new ArrayList<>();
        expectedList.add(expectedTeacher);

        Mockito.when(teacherRepository.findAllByOrderByLastNameAsc())
                .thenReturn(expectedList);

        List<Teacher> actualList = teacherService.findAllByOrderByLastNameAsc();

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(ArrayList.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
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
    public void noneException_whenDeleteTeacher() {

        teacherService.deleteTeacher(expectedTeacher.getId());
    }

}
