package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.StudyPlanDTO;
import io.github.externschool.planner.entity.GradeLevel;
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
    final SchoolSubjectService subjectService;

    @Autowired
    public StudyPlanController(final StudyPlanService planService,
                               final ConversionService conversionService,
                               final SchoolSubjectService subjectService) {
        this.planService = planService;
        this.conversionService = conversionService;
        this.subjectService = subjectService;
    }

    @GetMapping({"/"})
    public ModelAndView displayStudyPlanList() {

        return prepareModelAndView(0, 1L,0L);
    }

    private ModelAndView prepareModelAndView(Integer level, Long subId, Long editId) {
        List<StudyPlanDTO> plans = Optional.of((level == 0
                ? planService.findAll().stream()
                : planService.findAllByGradeLevel(GradeLevel.valueOf(level)).stream())
                .map(s -> conversionService.convert(s, StudyPlanDTO.class))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        ModelAndView modelAndView = new ModelAndView(
                "plan/plan_list",
                "plans", plans);
        modelAndView.addObject("level", level);
        modelAndView.addObject("subjects", subjectService.findAllByOrderByTitle());
        modelAndView.addObject("subjectid", subId);
        modelAndView.addObject("plan_id", editId);


        return modelAndView;
    }

    @GetMapping({"/grade/{level}"})
    public ModelAndView displayStudyPlanListByGrade(@PathVariable("level") Integer level) {

        return prepareModelAndView(level, 2L,0L);
    }

    @PostMapping("/{id}")
    public ModelAndView grade(@PathVariable Long id) {

        return prepareModelAndView(planService.findById(id).getGradeLevel().getValue(), 3L, id);
    }

    @PostMapping("/{id}/edit")
    public ModelAndView processSubjectListActionEdit(@PathVariable Long id,
                                                     @ModelAttribute("new_title") String newTitle) {
        Optional.ofNullable(planService.findById(id))
                .ifPresent(plan -> {
                    plan.setTitle(newTitle);
                    planService.saveOrUpdatePlan(plan);
                });

        return new ModelAndView("redirect:/plan/");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView processSubjectListActionDelete(@PathVariable ("id") Long id) {
        Optional.ofNullable(planService.findById(id))
                .ifPresent(planService::deletePlan);

        return new ModelAndView("redirect:/plan/");
    }

    @PostMapping("/add/subject/{sid}/grade/{gid}")
    public ModelAndView processSubjectListActionAdd(@PathVariable ("sid") Long subject_id,
                                                    @PathVariable ("gid") Integer grade_level,
                                                    @ModelAttribute("new_title") String title) {
        //TODO
        System.out.println("\n\n===========\n" + subject_id + "\n===========\n\n");

        StudyPlan plan = new StudyPlan();
        plan.setTitle(title);
        plan.setSubject(subjectService.findSubjectById(subject_id));
        plan.setGradeLevel(GradeLevel.valueOf(grade_level));
        planService.saveOrUpdatePlan(plan);

        return new ModelAndView("redirect:/plan/grade/" + grade_level);
    }
}
