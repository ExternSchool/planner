package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Gender;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/subject")
public class SubjectController {
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
    public SubjectController(final StudentService studentService,
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

    @GetMapping({"/"})
    public ModelAndView displayStudentList(Long editId) {
        ModelAndView modelAndView = new ModelAndView(
                "subject/subject_list",
                "subjects", Optional.ofNullable(subjectService.findAllByOrderByName())
                .orElse(Collections.emptyList()));
        modelAndView.addObject("editId", editId);

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView grade(@PathVariable Long id) {

        return displayStudentList(id);
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

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable ("id") Long id) {
        Optional.ofNullable(subjectService.findSubjectById(id))
                .ifPresent(subject -> subjectService.deleteSubjectById(id));

        return new ModelAndView("redirect:/subject/");
    }

    @PostMapping("/add")
    public ModelAndView add(@ModelAttribute("new_title") String title) {
        SchoolSubject subject = new SchoolSubject();
        subject.setTitle(title);
        subjectService.saveOrUpdateSubject(subject);

        return new ModelAndView("redirect:/subject/");
    }

    private ModelAndView showSubjectProfileForm(Long currentId) {
        ModelAndView modelAndView = new ModelAndView("subject/subject_form");
        modelAndView.addObject("subjects", subjectService.findAllByOrderByName());
        modelAndView.addObject("grades", Arrays.asList(GradeLevel.values()));
        modelAndView.addObject("deleteSubj", new SchoolSubject());

        return modelAndView;
    }
}
