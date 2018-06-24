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
        expectedTeacher.setFirstName("FirstName");
        expectedTeacher.setLastName("LastName");

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnTeacher_WhenFindTeacherById() {
        Mockito.when(teacherRepository.findTeacherById(expectedTeacher.getId()))
                .thenReturn(expectedTeacher);

        Teacher actualTeacher = teacherService.findTeacherById(expectedTeacher.getId());

        assertThat(actualTeacher)
                .isNotNull()
                .isEqualTo(expectedTeacher)
                .isEqualToComparingFieldByField(expectedTeacher);
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

}
