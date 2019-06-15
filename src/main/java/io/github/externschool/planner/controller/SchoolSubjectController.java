package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.service.SchoolSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_CONTROL;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_SUBJECT_EXISTS_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_SUBJECT_TITLE_MESSAGE;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/subject")
public class SchoolSubjectController {
    private final SchoolSubjectService subjectService;

    @Autowired
    public SchoolSubjectController(final SchoolSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/")
    public ModelAndView displaySubjectList(Long editId) {

        return prepareSubjectList(editId);
    }

    @PostMapping("/{id}")
    public ModelAndView grade(@PathVariable Long id) {

        return displaySubjectList(id);
    }

    @PostMapping("/{id}/edit")
    public ModelAndView processSubjectListActionEdit(@PathVariable Long id,
                                                     @ModelAttribute("new_title") String newTitle) {
        Optional.ofNullable(subjectService.findSubjectById(id))
                .ifPresent(subject -> {
                    subject.setTitle(newTitle);
                    subjectService.saveOrUpdateSubject(subject);
                });

        return new ModelAndView("redirect:/subject/");
    }

    @PostMapping("/add")
    public ModelAndView processSubjectListActionAdd(@ModelAttribute("new_title") String title) {
        try {
            if (title.isEmpty()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_SUBJECT_TITLE_MESSAGE);
            }
            if (subjectService.findSubjectByTitle(title) != null) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_SUBJECT_EXISTS_MESSAGE);
            }
        } catch (BindingResultException e) {
            ModelAndView modelAndView = prepareSubjectList(0L);
            modelAndView.addObject("error", e.getMessage());

            return modelAndView;
        }

        SchoolSubject subject = new SchoolSubject();
        subject.setTitle(title);
        subjectService.saveOrUpdateSubject(subject);

        return new ModelAndView("redirect:/subject/");
    }

    @GetMapping(value = "/{id}/delete-modal")
    public ModelAndView displaySubjectListDeleteModal(@PathVariable ("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("subject/subject_list :: deleteSchoolSubject");
        SchoolSubject subject = subjectService.findSubjectById(id);
        if (subject.getTitle().equals(UK_EVENT_TYPE_CONTROL)) {
            id = null;
        }
        modelAndView.addObject("editId", id);

        return modelAndView;
    }

    @PostMapping("/{id}/delete")
    public ModelAndView processSubjectListDelete(@PathVariable ("id") Long id) {
        Optional.ofNullable(subjectService.findSubjectById(id))
                .ifPresent(subject -> subjectService.deleteSubjectById(id));

        return new ModelAndView("redirect:/subject/");
    }

    private ModelAndView prepareSubjectList(Long editId) {
        ModelAndView modelAndView = new ModelAndView(
                "subject/subject_list",
                "subjects", Optional.ofNullable(subjectService.findAllByOrderByTitle())
                .orElse(Collections.emptyList()));
        modelAndView.addObject("editId", editId);

        return modelAndView;
    }
}
