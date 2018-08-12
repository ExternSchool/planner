package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudyPlanDTO;
import io.github.externschool.planner.entity.StudyPlan;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class StudyPlanDTOToStudyPlan implements Converter<StudyPlanDTO, StudyPlan> {


    @Override
    public StudyPlan convert(StudyPlanDTO studyPlanDTO) {
        StudyPlan studyPlan = new StudyPlan();
        BeanUtils.copyProperties(studyPlanDTO, studyPlan);
        return studyPlan;
    }
}
