package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.service.CourseService;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.StudyPlanService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import io.github.externschool.planner.util.CollatorHolder;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final PersonService personService;
    private final UserService userService;
    private final VerificationKeyService keyService;
    private final ConversionService conversionService;
    private final RoleService roleService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final StudyPlanService planService;

    @Autowired
    public StudentController(final StudentService studentService,
                             final PersonService personService,
                             final UserService userService,
                             final VerificationKeyService keyService,
                             final ConversionService conversionService,
                             final RoleService roleService,
                             final CourseService courseService,
                             final TeacherService teacherService,
                             final StudyPlanService planService) {
        this.studentService = studentService;
        this.personService = personService;
        this.userService = userService;
        this.keyService = keyService;
        this.conversionService = conversionService;
        this.roleService = roleService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.planService = planService;
    }

    /**
     * Displays List of All Students to Admin and Filtered By Id to Teacher
     * @param principal principal user
     * @return ModelAndView
     * */
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping("/")
    public ModelAndView displayStudentListToPrincipal(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        if (isTeacher(user)) {
            Long id = Optional.ofNullable(user)
                    .map(User::getVerificationKey)
                    .map(VerificationKey::getPerson)
                    .map(Person::getId)
                    .orElse(null);
            return prepareStudentListModelAndView(id, 0);
        }

        return prepareStudentListModelAndView(null, 0);
    }

    /**
     * Displays List of Students for All Grade Levels By Teacher Id
     * @param id Teacher Id
     * @return ModelAndView
     */
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping("/teacher/{id}")
    public ModelAndView displayAllStudentsListByTeacherId(@PathVariable("id") Long id) {
        return prepareStudentListModelAndView(id, 0);
    }

    /**
     * Displays List of Students for All Teachers By Grade Level
     * @param level Grade Level
     * @return ModelAndView
     */
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping({"/grade/{level}"})
    public ModelAndView displayStudentListByGrade(@PathVariable("level") Integer level) {
        return prepareStudentListModelAndView(null, level);
    }

    /**
     * Displays List of Students By Teacher Id and By Grade Level
     * @param id Teacher Id
     * @param level Grade Level
     * @return ModelAndView
     */
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping({"/teacher/{id}/grade/{level}"})
    public ModelAndView displayStudentListByTeacherIdByGrade(@PathVariable("id") Long id,
                                                             @PathVariable("level") Integer level) {
        return prepareStudentListModelAndView(id, level);
    }

    @Secured("ROLE_STUDENT")
    @GetMapping("/profile")
    public ModelAndView displayFormStudentProfile(final Principal principal) {
        final User user = userService.getUserByEmail(principal.getName());
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

    @Secured("ROLE_STUDENT")
    @GetMapping("/plan")
    public ModelAndView displayFormStudentPlanForStudent(final Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Student student = studentService.findStudentById(user.getVerificationKey().getPerson().getId());
        List<Course> courses = courseService.selectCoursesForStudent(student);

        return showStudentPlanForm(student, courses, 0L);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/plan")
    public ModelAndView showStudentPlanForm(@PathVariable("id") Long id,
                                            final Principal principal) {
        Student student = studentService.findStudentById(id);
        User user = userService.getUserByEmail(principal.getName());
        if (student == null) {
            return redirectByRole(user);
        }
        List<Course> courses = courseService.selectCoursesForStudent(student);

        return showStudentPlanForm(student, courses, 0L);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{sid}/plan/{id}")
    public ModelAndView showStudentPlanFormToEditTeacher(@PathVariable("sid") Long sid,
                                                         @PathVariable("id") Long id) {
        Student student = studentService.findStudentById(sid);
        if (student == null) {
            return new ModelAndView("redirect:/student/");
        }
        List<Course> courses = courseService.findAllByStudentId(sid);
        Long coursePlanId = Optional.ofNullable(courseService.findCourseByStudentIdAndPlanId(sid, id).getPlanId())
                .orElse(0L);

        return showStudentPlanForm(student, courses, coursePlanId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{sid}/plan/{id}", params = "action=teacher")
    public ModelAndView processStudentPlanFormActionTeacher(@PathVariable("sid") Long sid,
                                                            @PathVariable("id") Long id,
                                                            @ModelAttribute("course") CourseDTO courseDTO) {
        Course course = courseService.findCourseByStudentIdAndPlanId(sid, id);
        course.setTeacher(courseDTO.getTeacher());
        courseService.saveOrUpdateCourse(course);
        List<Course> courses = courseService.findAllByStudentId(sid);

        return showStudentPlanForm(studentService.findStudentById(sid), courses, 0L);
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

        return redirectByRole(userService.getUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @GetMapping(value = "/cancel/{kid}")
    public ModelAndView processFormStudentProfileCancel(@PathVariable("kid") Long keyId, Principal principal) {
        VerificationKey key = keyService.findKeyById(keyId);
        if (key != null
                && (key.getPerson() == null
                || key.getPerson().getId() == null
                || personService.findPersonById(key.getPerson().getId()) == null)) {
            keyService.deleteById(key.getId());
        }

        return redirectByRole(userService.getUserByEmail(principal.getName()));
    }

    /**
     * Assigns new Verification Key to the Student whose Id provided
     * When key change confirmed:
     *         DTO Receives a NEW KEY which is instantly assigned,
     *         an old key is removed from user (if present),
     *         user receives Guest role
     *
     * @param id Student Id
     * @return ModelAndView
     */
    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{id}/new-key")
    public ModelAndView processStudentProfileFormActionNewKey(@PathVariable("id") Long id) {
        StudentDTO studentDTO = Optional.ofNullable(studentService.findStudentById(id))
                .map(student -> conversionService.convert(student, StudentDTO.class))
                .map(s -> (StudentDTO)keyService.setNewKeyToDTO(s))
                .orElse(new StudentDTO());

        Optional.ofNullable(userService.getUserByEmail(studentDTO.getEmail()))
                .ifPresent(user -> {
                    userService.createNewKeyWithNewPersonAndAddToUser(user);
                    userService.save(user);
                });

        return showStudentProfileForm(studentDTO, true);
    }

    private ModelAndView prepareStudentListModelAndView(Long teacherId, Integer level) {
        List<StudentDTO> students = getStudentListByTeacherIdAndGradeLevel(teacherId, level);
        ModelAndView modelAndView = new ModelAndView(
                "student/student_list",
                "students", students);
        modelAndView.addObject("level", level);
        modelAndView.addObject("teacherId", teacherId);

        return modelAndView;
    }

    private List<StudentDTO> getStudentListByTeacherIdAndGradeLevel(Long teacherId, int level) {
        if (teacherId == null) {
            List<StudentDTO> allStudents = studentService.findAllStudents().stream()
                    .sorted(Comparator.comparing(
                            Student::getLastName,
                            Comparator.nullsLast(CollatorHolder.getUaCollator())))
                    .map(student -> conversionService.convert(student, StudentDTO.class))
                    .collect(Collectors.toList());
            if (level != 0) {
                return allStudents.stream()
                        .filter(student -> level == student.getGradeLevel())
                        .collect(Collectors.toList());
            }

            return allStudents;
        }

        List<StudentDTO> studentsByTeacherId = new ArrayList<>();
        for (Course course : courseService.findAllByTeacherId(teacherId)) {
            String title = Optional.ofNullable(course.getPlanId())
                    .map(planService::findById)
                    .map(StudyPlan::getSubject)
                    .map(SchoolSubject::getTitle)
                    .orElse("");
            Optional<StudentDTO> optional = Optional.ofNullable(course.getStudentId())
                    .map(studentService::findStudentById)
                    .map(student -> conversionService.convert(student, StudentDTO.class));
            if (optional.isPresent()) {
                StudentDTO studentDTO = optional.get();
                studentDTO.setOptionalData(title);
                studentsByTeacherId.add(studentDTO);
            }
        }
        studentsByTeacherId = studentsByTeacherId.stream()
                .sorted(Comparator.comparing(
                        StudentDTO::getLastName,
                        Comparator.nullsLast(CollatorHolder.getUaCollator())))
                .collect(Collectors.toList());

        if (level != 0) {
            return studentsByTeacherId.stream()
                    .filter(student -> level == student.getGradeLevel())
                    .collect(Collectors.toList());
        }

        return studentsByTeacherId;
    }

    private ModelAndView redirectByRole(User user) {
        if (user != null && user.getEmail() != null) {
            User userFound = userService.getUserByEmail(user.getEmail());
            if (userFound != null && userFound.getRoles().contains(roleService.getRoleByName("ROLE_ADMIN"))) {

                return new ModelAndView("redirect:/student/");
            }
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

    private ModelAndView showStudentPlanForm(Student student, List<Course> courses, Long coursePlanId) {
        ModelAndView modelAndView = new ModelAndView("student/student_plan");
        String studentData = student.getLastName() + " " +
                student.getFirstName() + " " +
                student.getPatronymicName() + ", " +
                student.getGradeLevel().toString();
        modelAndView.addObject("studentData", studentData);
        modelAndView.addObject("studentId", student.getId());
        List<CourseDTO> courseDTOs = courses.stream()
                .map(c -> conversionService.convert(c, CourseDTO.class))
                .filter(Objects::nonNull)
                .peek(courseDTO -> {
                    StudyPlan plan = planService.findById(courseDTO.getPlanId());
                    courseDTO.setHoursPerSemesterOne(plan.getHoursPerSemesterOne());
                    courseDTO.setHoursPerSemesterTwo(plan.getHoursPerSemesterTwo());
                    courseDTO.setWorksPerSemesterOne(plan.getWorksPerSemesterOne());
                    courseDTO.setWorksPerSemesterTwo(plan.getWorksPerSemesterTwo());
                })
                .collect(Collectors.toList());
        modelAndView.addObject("courses", courseDTOs);
        modelAndView.addObject("course",
                Optional.ofNullable(courseService.findCourseByStudentIdAndPlanId(student.getId(), coursePlanId))
                        .map(course -> conversionService.convert(course, CourseDTO.class))
                        .orElse(new CourseDTO(0L, 0L)));
        List<Teacher> teachers = new ArrayList<>(Optional.ofNullable(planService.findById(coursePlanId))
                .map(StudyPlan::getSubject)
                .map(teacherService::findAllBySubject)
                .orElse(Collections.emptyList()));
        teacherService.findAllByLastName(UK_COURSE_NO_TEACHER).stream().findAny().ifPresent(teachers::add);
        modelAndView.addObject("teachers", teachers);
        modelAndView.addObject("coursePlanId", coursePlanId);

        return modelAndView;
    }

    private Boolean isTeacher(User user) {
        return Optional.ofNullable(user)
                .map(User::getEmail)
                .map(userService::getUserByEmail)
                .map(User::getRoles)
                .map(roles -> roles.contains(roleService.getRoleByName("ROLE_TEACHER")))
                .orElse(Boolean.FALSE);
    }

    private Optional<Teacher> getTeacher(Principal principal) {
        return Optional.ofNullable(principal.getName())
                .map(userService::getUserByEmail)
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .map(Person::getId)
                .map(teacherService::findTeacherById);
    }
}
