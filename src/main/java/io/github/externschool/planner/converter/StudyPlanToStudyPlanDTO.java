package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudyPlanDTO;
import io.github.externschool.planner.entity.StudyPlan;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class StudyPlanToStudyPlanDTO implements Converter<StudyPlan, StudyPlanDTO> {
    @Override
    public StudyPlanDTO convert(StudyPlan studyPlan) {
        StudyPlanDTO studyPlanDTO = new StudyPlanDTO();
        BeanUtils.copyProperties(studyPlan, studyPlanDTO);

        return studyPlanDTO;
    }
}
