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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_TEST;

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
        return prepareModelAndView(level == null ? 0 : level,0L);
    }

    @GetMapping("/grade/{level}")
    public ModelAndView displayStudyPlansListByGrade(@PathVariable("level") Integer level) {
        return prepareModelAndView(level == null ? 0 : level,0L);
    }

    @GetMapping("/{id}")
    public ModelAndView  displayStudyPlansListActionEdit(@PathVariable Long id) {
        if (id == null || planService.findById(id) == null || planService.findById(id).getGradeLevel() == null) {
            return prepareModelAndView(0, 0L);
        }
        return prepareModelAndView(planService.findById(id).getGradeLevel().getValue(), id);
    }

    @PostMapping(value = "/", params = "action=add")
    public ModelAndView processStudyPlansListActionAdd(@ModelAttribute("plan") StudyPlanDTO planDTO) {
        ModelAndView modelAndView = new ModelAndView("redirect:/plan/");
        Optional.ofNullable(subjectService.findSubjectById(planDTO.getSubject().getId()))
                .ifPresent(subject -> {
                    StudyPlan plan = new StudyPlan(planDTO.getGradeLevel(), subject, subject.getTitle(),
                            0, 0, 0, 0);
                    plan = planService.saveOrUpdatePlan(plan);
                    modelAndView.addObject("level", plan.getGradeLevel().getValue());
                    modelAndView.setViewName(modelAndView.getViewName() + "grade/" + plan.getGradeLevel().getValue());
                });

        return modelAndView;
    }

    @PostMapping(value = "/", params = "action=save")
    public ModelAndView processStudyPlansActionSave(@ModelAttribute("plan") StudyPlanDTO planDTO) {
        ModelAndView modelAndView = new ModelAndView("redirect:/plan/");
        Optional.ofNullable(planService.findById(planDTO.getId()))
                .ifPresent(plan -> {
                    planService.saveOrUpdatePlan(conversionService.convert(planDTO, StudyPlan.class));
                    modelAndView.addObject("level", plan.getGradeLevel().getValue());
                    modelAndView.setViewName(modelAndView.getViewName() + "grade/" + plan.getGradeLevel().getValue());
                });

        return modelAndView;
    }

    @GetMapping(value = "/{id}/delete-modal")
    public ModelAndView displayPlanListDeleteModal(@PathVariable ("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("plan/plan_list :: deleteStudyPlan");
        StudyPlan plan = planService.findById(id);
        StudyPlanDTO dto = conversionService.convert(plan, StudyPlanDTO.class);
        if (plan.getTitle().equals(UK_EVENT_TYPE_TEST)) {
            dto.setId(null);
        }
        modelAndView.addObject("plan", dto);

        return modelAndView;
    }

    @PostMapping(value = "/{id}/delete")
    public ModelAndView processPlanListDelete(@PathVariable ("id") Long id) {
        Optional.ofNullable(planService.findById(id))
                .ifPresent(planService::deletePlan);

        return new ModelAndView("redirect:/plan/");
    }

    private ModelAndView prepareModelAndView(Integer level, Long planId) {
        if (level == null) {
            level = 0;
        }
        if (planId == null) {
            planId = 0L;
        }
        List<StudyPlanDTO> plans = Optional.of((level == 0
                    ? planService.findAll().stream()
                    : planService.findAllByGradeLevel(GradeLevel.valueOf(level)).stream())
                .filter(Objects::nonNull)
                .filter(s -> !s.getTitle().isEmpty() && !s.getTitle().equals(UK_EVENT_TYPE_TEST))
                .map(s -> conversionService.convert(s, StudyPlanDTO.class))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        ModelAndView modelAndView = new ModelAndView("plan/plan_list", "plans", plans);
        modelAndView.addObject("plan", conversionService.convert(
                Optional.ofNullable(planService.findById(planId))
                        .orElse(new StudyPlan(GradeLevel.valueOf(level), new SchoolSubject())),
                StudyPlanDTO.class));
        List<SchoolSubject> subjects = subjectService.findAllByOrderByTitle().stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.getTitle().isEmpty() && !s.getTitle().equals(UK_EVENT_TYPE_TEST))
                .collect(Collectors.toList());
        modelAndView.addObject("subjects", subjects);
        modelAndView.addObject("level", GradeLevel.valueOf(level));

        return modelAndView;
    }
}
