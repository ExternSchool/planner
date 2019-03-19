package io.github.externschool.planner.converter;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.dto.StudyPlanDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class StudyPlanConverterTest {
    @Autowired ConversionService conversionService;

    private StudyPlan studyPlan;
    private StudyPlanDTO studyPlanDTO;

    @Before
    public void setup(){
        final long id = 1L;
        final GradeLevel gradeLevel = GradeLevel.valueOf(1);
        final SchoolSubject schoolSubject = new SchoolSubject();
        schoolSubject.setTitle("Math");
        final String title = "Study plan";
        final Integer hoursPerSemesterOne = 30;
        final Integer hoursPerSemesterTwo = 40;
        final Integer worksPerSemesterOne = 1;
        final Integer worksPerSemesterTwo = 0;

        studyPlan = new StudyPlan();
        studyPlan.setId(id);
        studyPlan.setGradeLevel(gradeLevel);
        studyPlan.setSubject(schoolSubject);
        studyPlan.setTitle(title);
        studyPlan.setHoursPerSemesterOne(hoursPerSemesterOne);
        studyPlan.setHoursPerSemesterTwo(hoursPerSemesterTwo);
        studyPlan.setWorksPerSemesterOne(worksPerSemesterOne);
        studyPlan.setWorksPerSemesterTwo(worksPerSemesterTwo);

        studyPlanDTO = new StudyPlanDTO();
        studyPlanDTO.setId(id);
        studyPlanDTO.setGradeLevel(gradeLevel);
        studyPlanDTO.setSubject(schoolSubject);
        studyPlanDTO.setTitle(title);
        studyPlanDTO.setHoursPerSemesterOne(hoursPerSemesterOne);
        studyPlanDTO.setHoursPerSemesterTwo(hoursPerSemesterTwo);
        studyPlanDTO.setWorksPerSemesterOne(worksPerSemesterOne);
        studyPlanDTO.setWorksPerSemesterTwo(worksPerSemesterTwo);
    }

    @Test
    public void shouldReturnStudyPlanDTO(){
        StudyPlanDTO actualStudyPlanDTO = conversionService.convert(studyPlan, StudyPlanDTO.class);

        assertThat(actualStudyPlanDTO).isNotNull()
                .isEqualTo(studyPlanDTO)
                .isEqualToComparingFieldByField(studyPlanDTO);
    }

    @Test
    public void shouldReturnStudyPlan(){
        StudyPlan actualStudyPlan = conversionService.convert(studyPlanDTO, StudyPlan.class);

        assertThat(actualStudyPlan).isNotNull()
                .isEqualTo(studyPlan)
                .isEqualToComparingFieldByField(studyPlan);
    }
}
