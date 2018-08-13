package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.service.CourseService;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.StudyPlanService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final PersonService personService;
    private final UserService userService;
    private final SchoolSubjectService subjectService;
    private final VerificationKeyService keyService;
    private final ConversionService conversionService;
    private final RoleService roleService;
    private final CourseService courseService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudyPlanService planService;

    @Autowired
    public StudentController(final StudentService studentService,
                             final PersonService personService,
                             final UserService userService,
                             final SchoolSubjectService subjectService,
                             final VerificationKeyService keyService,
                             final ConversionService conversionService,
                             final RoleService roleService,
                             final CourseService courseService) {
        this.studentService = studentService;
        this.personService = personService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.keyService = keyService;
        this.conversionService = conversionService;
        this.roleService = roleService;
        this.courseService = courseService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping({"/"})
    public ModelAndView displayStudentList() {
        ModelAndView modelAndView = new ModelAndView(
                "student/student_list",
                "students", Optional.ofNullable(studentService.findAllByOrderByLastName().stream()
                .map(s -> conversionService.convert(s, StudentDTO.class))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList()));
        modelAndView.addObject("level", 0);

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping({"/grade/{level}"})
    public ModelAndView displayStudentListByGrade(@PathVariable("level") Integer level) {
        List<StudentDTO> list = Optional.ofNullable(studentService.findAllByGradeLevel(GradeLevel.valueOf(level)).stream()
                .map(s -> conversionService.convert(s, StudentDTO.class))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        ModelAndView modelAndView = new ModelAndView(
                "student/student_list",
                "students", list);
        modelAndView.addObject("level", level);

        return modelAndView;
    }

    @Secured("ROLE_STUDENT")
    @GetMapping("/profile")
    public ModelAndView displayFormStudentProfile(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();
        StudentDTO studentDTO = conversionService.convert(studentService.findStudentById(id), StudentDTO.class);

        return showStudentProfileForm(studentDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayFormStudentProfileToEdit(@PathVariable("id") Long id) {
        StudentDTO studentDTO = conversionService.convert(studentService.findStudentById(id), StudentDTO.class);

        return showStudentProfileForm(studentDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayFormStudentProfileToAdd() {
        StudentDTO studentDTO = new StudentDTO();
        keyService.setNewKeyToDTO(studentDTO);

        return showStudentProfileForm(studentDTO, true);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id) {
        //TODO Add deletion confirmation
        studentService.deleteStudentById(id);

        return new ModelAndView("redirect:/student/");
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormStudentProfileActionSave(@ModelAttribute("student") @Valid StudentDTO studentDTO,
                                                            BindingResult bindingResult,
                                                            Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
        } catch (BindingResultException e) {
            ModelAndView modelAndView = showStudentProfileForm(studentDTO, true);
            modelAndView.addObject("error", e.getMessage());

            return modelAndView;
        }
        studentService.saveOrUpdateStudent(conversionService.convert(studentDTO, Student.class));

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @GetMapping(value = "/update/cancel/{id}")
    public ModelAndView processFormStudentProfileActionCancel(@PathVariable("id") Long keyId,
                                                              Principal principal) {
        VerificationKey key = keyService.findKeyById(keyId);
        if (key != null
                && (key.getPerson() == null
                || key.getPerson().getId() == null
                || personService.findPersonById(key.getPerson().getId()) == null)) {
            keyService.deleteById(key.getId());
        }

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/update", params = "action=newKey")
    public ModelAndView processFormStudentProfileActionNewKey(@ModelAttribute("student") StudentDTO studentDTO) {
        //TODO Add key change confirmation
        /*
        When key change confirmed:
        DTO Receives NEW KEY which is instantly assigned, it CAN'T BE CANCELLED even if Cancel button pressed.
        An old key is removed from user (if present), user receives role of Guest
         */
        studentDTO = (StudentDTO)keyService.setNewKeyToDTO(studentDTO);
        Optional.ofNullable(userService.findUserByEmail(studentDTO.getEmail()))
                .ifPresent(user -> {
                    userService.assignNewRolesByKey(user, user.getVerificationKey());
                    userService.saveOrUpdate(user);
                });

        return showStudentProfileForm(studentDTO, true);
    }

    private ModelAndView redirectByRole(User user) {
        if (userService.findUserByEmail(user.getEmail())
                .getRoles()
                .contains(roleService.getRoleByName("ROLE_ADMIN"))) {

            return new ModelAndView("redirect:/student/");
        }

        return new ModelAndView("redirect:/");
    }

    private ModelAndView showStudentProfileForm(StudentDTO studentDTO, Boolean isNew) {
        ModelAndView modelAndView = new ModelAndView("student/student_profile");
        modelAndView.addObject("student", studentDTO);
        modelAndView.addObject("grades", Arrays.asList(GradeLevel.values()));
        modelAndView.addObject("genders", Arrays.asList(Gender.values()));
        modelAndView.addObject("isNew", isNew);

        return modelAndView;
    }
}
