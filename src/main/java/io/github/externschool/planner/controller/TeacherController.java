package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final SchoolSubjectService subjectService;
    private final ConversionService conversionService;

    public TeacherController(final TeacherService teacherService,
                             final SchoolSubjectService subjectService,
                             final ConversionService conversionService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.conversionService = conversionService;
    }

    @GetMapping("/")
    public ModelAndView findAll() {
        return new ModelAndView(
                "teacher/teacher_list",
                "teachers",
                teacherService.findAllByOrderByLastNameAsc().stream()
                        .map(t -> conversionService.convert(t, TeacherDTO.class))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ModelAndView findOne(@PathVariable("id") Long id) {
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

        return show(teacherDTO);
    }

    @GetMapping("/add")
    public ModelAndView add() {
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher());
        TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);

        return show(teacherDTO);
    }

    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView save(@ModelAttribute("teacher") TeacherDTO teacherDTO, Model model) {
        teacherService.saveOrUpdateTeacher(conversionService.convert(teacherDTO,Teacher.class));

        return new ModelAndView("redirect:/teacher/");
    }

    @PostMapping(value = "/update", params = "action=newKey")
    public ModelAndView newKey(@ModelAttribute("teacher") TeacherDTO teacherDTO, Model model) {
        teacherDTO = newKey(teacherDTO);

        return show(teacherDTO);
    }

    @GetMapping(value = "/update")
    public ModelAndView cancel() {
        return new ModelAndView("redirect:/teacher/");
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id) {
        teacherService.deleteTeacher(id);

        return new ModelAndView("redirect:/teacher/");
    }

    private TeacherDTO newKey(TeacherDTO teacherDTO) {
        //TODO add key change confirmation request
        //TODO move it to a key service
        teacherDTO.setVerificationKey(UUID.randomUUID().toString());

        return teacherDTO;
    }

    private ModelAndView show(TeacherDTO teacherDTO) {
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_profile");
        modelAndView.addObject("teacher", teacherDTO);
        modelAndView.addObject("allSubjects", subjectService.findAllByOrderByNameAsc());

        return modelAndView;
    }
}
