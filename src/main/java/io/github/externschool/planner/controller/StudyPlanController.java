package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.StudyPlanDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/plan")
public class StudyPlanController {
    private final StudyPlanService planService;
    private final ConversionService conversionService;
    private final SchoolSubjectService subjectService;

    @Autowired
    public StudyPlanController(final StudyPlanService planService,
                               final ConversionService conversionService,
                               final SchoolSubjectService subjectService) {
        this.planService = planService;
        this.conversionService = conversionService;
        this.subjectService = subjectService;
    }

    @GetMapping("/")
    public ModelAndView displayAllStudyPlansList(Integer level) {
        return prepareModelAndView(Optional.ofNullable(level).orElse(0),0L);
    }

    @GetMapping("/grade/{level}")
    public ModelAndView displayStudyPlansListByGrade(@PathVariable("level") Integer level) {
        return displayAllStudyPlansList(level);
    }

    private ModelAndView prepareModelAndView(Integer level, Long planId) {
        List<StudyPlanDTO> plans = Optional.of((level == 0
                ? planService.findAll().stream()
                : planService.findAllByGradeLevel(GradeLevel.valueOf(level)).stream())
                    .map(s -> conversionService.convert(s, StudyPlanDTO.class))
                    .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        ModelAndView modelAndView = new ModelAndView("plan/plan_list", "plans", plans);
        modelAndView.addObject("plan", conversionService.convert(
                Optional.ofNullable(planService.findById(planId))
                        .orElse(new StudyPlan()),
                StudyPlanDTO.class));
        List<SchoolSubject> subjects = subjectService.findAllByOrderByTitle();
        modelAndView.addObject("subjects", subjects);
        modelAndView.addObject("subject_id", Optional.ofNullable(subjects.get(0).getId()).orElse(0L));
        modelAndView.addObject("level", level);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView grade(@PathVariable Long id) {
        return prepareModelAndView(planService.findById(id).getGradeLevel().getValue(), id);
    }

    @PostMapping("/{id}/edit")
    public ModelAndView processSubjectListActionEdit(@PathVariable ("id") Long id,
                                                     @ModelAttribute("new_title") String title) {
        ModelAndView modelAndView = new ModelAndView("redirect:/plan/");
        if (!title.isEmpty()) {
            Optional.ofNullable(planService.findById(id))
                    .ifPresent(plan -> {
                        plan.setTitle(title);
                        planService.saveOrUpdatePlan(plan);
                        modelAndView.addObject("level", plan.getGradeLevel().getValue());
                    });
        }

        return modelAndView;
    }

    @PostMapping("/{id}/delete")
    public ModelAndView processSubjectListActionDelete(@PathVariable ("id") Long id) {
        Optional.ofNullable(planService.findById(id))
                .ifPresent(planService::deletePlan);

        return new ModelAndView("redirect:/plan/");
    }

    @GetMapping("/add/subject/{sid}/grade/{gid}")
    public ModelAndView processSubjectListActionAdd(@PathVariable ("sid") Long subject_id,
                                                    @PathVariable ("gid") Integer grade_level) {
        Optional.ofNullable(subjectService.findSubjectById(subject_id)).ifPresent(subject -> {
            StudyPlan plan = new StudyPlan();
            plan.setSubject(subject);
            plan.setGradeLevel(GradeLevel.valueOf(grade_level));
            plan.setTitle(subject.getTitle());
            planService.saveOrUpdatePlan(plan);
        });

        return new ModelAndView("redirect:/plan/grade/" + grade_level);
    }
}
