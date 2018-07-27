package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final SchoolSubjectService subjectService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;

    public TeacherController(final TeacherService teacherService,
                             final SchoolSubjectService subjectService,
                             final ConversionService conversionService,
                             final VerificationKeyService keyService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.conversionService = conversionService;
        this.keyService = keyService;
    }

    @GetMapping("/")
    public ModelAndView findAll() {
        return new ModelAndView(
                "teacher/teacher_list",
                "teachers",
                teacherService.findAllByOrderByLastName().stream()
                        .map(t -> conversionService.convert(t, TeacherDTO.class))
                        .collect(Collectors.toList()));
    }

    @PostMapping("/{id}")
    public ModelAndView edit(@PathVariable("id") Long id) {
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

        return show(teacherDTO);
    }

    @PostMapping("/add")
    public ModelAndView add() {

        return show(new TeacherDTO());
    }

    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView save(@ModelAttribute("teacher") TeacherDTO teacherDTO, Model model) {
        if (teacherDTO.getId() == null || teacherService.findTeacherById(teacherDTO.getId()) == null) {
            if (teacherDTO.getVerificationKey() == null) {
                teacherDTO.setVerificationKey(new VerificationKey());
            }
            keyService.saveOrUpdateKey(teacherDTO.getVerificationKey());
        }
        Teacher teacher = conversionService.convert(teacherDTO, Teacher.class);
        teacherService.saveOrUpdateTeacher(teacher);

        return new ModelAndView("redirect:/teacher/");
    }

    @PostMapping(value = "/update", params = "action=newKey")
    public ModelAndView newKey(@ModelAttribute("teacher") TeacherDTO teacherDTO) {
        teacherDTO = setNewKey(teacherDTO);

        return show(teacherDTO);
    }

    @GetMapping(value = "/update")
    public ModelAndView cancel() {
        return new ModelAndView("redirect:/teacher/");
    }

    @GetMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id) {
        teacherService.deleteTeacher(id);

        return new ModelAndView("redirect:/teacher/");
    }

    private TeacherDTO setNewKey(TeacherDTO teacherDTO) {
        //TODO add key change confirmation request

        return (TeacherDTO)keyService.setNewKeyToDTO(teacherDTO);
    }

    private ModelAndView show(TeacherDTO teacherDTO) {
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_profile", "teacher", teacherDTO);
        modelAndView.addObject("isNew", isNew(teacherDTO));
        modelAndView.addObject("allSubjects", subjectService.findAllByOrderByName());

        return modelAndView;
    }

    private Boolean isNew(TeacherDTO teacherDTO) {
        return teacherService.findTeacherById(teacherDTO.getId()) == null;
    }
}
