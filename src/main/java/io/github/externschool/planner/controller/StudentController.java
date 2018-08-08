package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final ConversionService conversionService;
    private final StudentService studentService;
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private VerificationKeyService keyService;
    @Autowired private PersonService personService;

    public StudentController(final ConversionService conversionService,
                             final StudentService studentService) {
        this.conversionService = conversionService;
        this.studentService = studentService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping({"/"})
    public ModelAndView displayStudentList() {
        return new ModelAndView(
                "student/student_list",
                "students", studentService.findAllByOrderByLastName().stream()
                .map(s -> conversionService.convert(s, StudentDTO.class))
                .collect(Collectors.toList()));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayFormStudentProfileToEdit(@PathVariable("id") Long id) {
        StudentDTO studentDTO = conversionService.convert(studentService.findStudentById(id), StudentDTO.class);

        return showStudentProfile(studentDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayFormStudentProfileToAdd() {
        StudentDTO studentDTO = new StudentDTO();
        keyService.setNewKeyToDTO(studentDTO);

        return showStudentProfile(studentDTO, true);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);

        return new ModelAndView("redirect:/student/");
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormStudentProfileActionSave(@ModelAttribute("student") @Valid StudentDTO studentDTO,
                                                            BindingResult bindingResult,
                                                            Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException("There are errors in form validation");
            }
        } catch (BindingResultException e) {
            ModelAndView modelAndView = showStudentProfile(studentDTO, true);
            modelAndView.addObject("error", e.getMessage());

            return modelAndView;
        }
        studentService.saveOrUpdateStudent(conversionService.convert(studentDTO, Student.class));

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView processFormStudentProfileActionCancel(@ModelAttribute("student") StudentDTO studentDTO,
                                                              Principal principal) {
        VerificationKey key = studentDTO.getVerificationKey();
        if (key.getPerson() == null
                || key.getPerson().getId() == null
                || personService.findPersonById(key.getPerson().getId()) == null) {
            keyService.deleteById(key.getId());
        }

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/update", params = "action=newKey")
    public ModelAndView processFormStudentProfileActionNewKey(@ModelAttribute("student") StudentDTO studentDTO) {
        //TODO add key change confirmation
        studentDTO = (StudentDTO)keyService.setNewKeyToDTO(studentDTO);

        return showStudentProfile(studentDTO, true);
    }

    private ModelAndView redirectByRole(User user) {
        if (userService.findUserByEmail(user.getEmail())
                .getRoles()
                .contains(roleService.getRoleByName("ROLE_ADMIN"))) {

            return new ModelAndView("redirect:/student/");
        }

        return new ModelAndView("redirect:/");
    }

    private ModelAndView showStudentProfile(StudentDTO studentDTO, Boolean isNew) {
        ModelAndView modelAndView = new ModelAndView("student/student_profile");
        modelAndView.addObject("student", studentDTO);
        modelAndView.addObject("subjects", subjectService.findAllByOrderByName());
        modelAndView.addObject("grades", Arrays.asList(GradeLevel.values()));
        modelAndView.addObject("genders", Arrays.asList(Gender.values()));
        modelAndView.addObject("isNew", isNew);


        return modelAndView;
    }

    private Boolean isNew(StudentDTO studentDTO) {
        return studentService.findStudentById(studentDTO.getId()) == null;
    }
}