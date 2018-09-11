package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
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

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final SchoolSubjectService subjectService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public TeacherController(final TeacherService teacherService,
                             final SchoolSubjectService subjectService,
                             final ConversionService conversionService,
                             final VerificationKeyService keyService,
                             final UserService userService,
                             final RoleService roleService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/")
    public ModelAndView showTeacherListForm() {
        List<Teacher> teachers = teacherService.findAllByOrderByLastName();
        Optional.ofNullable(teacherService.findAllByLastName(UK_COURSE_NO_TEACHER))
                .ifPresent(t -> t.forEach(teachers::remove));
        List<TeacherDTO> teacherDTOs = teachers.stream()
                .filter(Objects::nonNull)
                .map(teacher -> conversionService.convert(teacher, TeacherDTO.class))
                .collect(Collectors.toList());

        return new ModelAndView("teacher/teacher_list", "teachers", teacherDTOs);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/profile")
    public ModelAndView showTeacherProfileForTeacher(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

        return showTeacherProfile(teacherDTO);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/schedule")
    public ModelAndView showTeacherScheduleToTeacher(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();

        return new ModelAndView("redirect:/teacher/" + id + "/schedule");
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/schedule")
    public ModelAndView showTeacherSchedule(@PathVariable("id") Long id) {
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_schedule",
                "teacher", teacherDTO);

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/schedule-modal", params = "action=save")
    public ModelAndView processTeacherScheduleModalFormSave(@PathVariable("id") Long id,
                                                      @ModelAttribute("teacher") TeacherDTO teacherDTO,
                                                      final Principal principal) {
        return new ModelAndView("teacher/teacher_schedule", "teacher", teacherDTO);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView showTeacherProfileToEdit(@PathVariable("id") Long id) {
        Teacher teacher = teacherService.findTeacherById(id);
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

        return showTeacherProfile(teacherDTO);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView showTeacherProfileToAdd() {
        return showTeacherProfile(new TeacherDTO());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView processTeacherListFormDelete(@PathVariable("id") Long id) {
        //TODO Add deletion confirmation
        teacherService.deleteTeacherById(id);

        return new ModelAndView("redirect:/teacher/");
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processTeacherProfileFormSave(@ModelAttribute("teacher") TeacherDTO teacherDTO,
                                                      final Principal principal) {
        if (teacherDTO.getId() == null || teacherService.findTeacherById(teacherDTO.getId()) == null) {
            if (teacherDTO.getVerificationKey() == null) {
                teacherDTO.setVerificationKey(new VerificationKey());
            }
            keyService.saveOrUpdateKey(teacherDTO.getVerificationKey());
        }
        Teacher teacher = conversionService.convert(teacherDTO, Teacher.class);
        teacherService.saveOrUpdateTeacher(teacher);

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping(value = "/update")
    public ModelAndView processTeacherProfileFormCancel(final Principal principal) {
        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{id}/new-key")
    public ModelAndView processTeacherProfileFormActionNewKey(@PathVariable("id") Long id) {
        //When key change confirmed:
        //DTO Receives a NEW KEY which is instantly assigned, an old key is removed from user (if present),
        //user receives Guest role
        TeacherDTO teacherDTO = Optional.ofNullable(teacherService.findTeacherById(id))
                .filter(Objects::nonNull)
                .map(teacher -> conversionService.convert(teacher, TeacherDTO.class))
                .map(t -> (TeacherDTO)keyService.setNewKeyToDTO(t))
                .orElse(new TeacherDTO());

        Optional.ofNullable(userService.findUserByEmail(teacherDTO.getEmail()))
                .ifPresent(user -> {
                    userService.createAndAddNewKeyAndPerson(user);
                    userService.saveOrUpdate(user);
                });

        ModelAndView modelAndView = showTeacherProfile(teacherDTO);
        modelAndView.addObject("isNew", true);

        return modelAndView;
    }

    private ModelAndView showTeacherProfile(TeacherDTO teacherDTO) {
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_profile",
                "teacher", teacherDTO);
        modelAndView.addObject("isNew", isNew(teacherDTO));
        modelAndView.addObject("allSubjects", subjectService.findAllByOrderByTitle());

        return modelAndView;
    }

    private Boolean isNew(TeacherDTO teacherDTO) {
        return !Optional.ofNullable(teacherDTO)
                .filter(Objects::nonNull)
                .map(t -> Optional.ofNullable(teacherService.findTeacherById(t.getId())).isPresent())
                .orElse(false);
    }

    private ModelAndView redirectByRole(User user) {
        if (user != null && user.getEmail() != null) {
            User userFound = userService.findUserByEmail(user.getEmail());
            if (userFound != null && userFound.getRoles().contains(roleService.getRoleByName("ROLE_ADMIN"))) {

                return new ModelAndView("redirect:/teacher/");
            }
        }

        return new ModelAndView("redirect:/");
    }
}
