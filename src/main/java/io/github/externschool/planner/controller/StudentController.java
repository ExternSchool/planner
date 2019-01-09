package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.dto.ParticipantDTO;
import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.exceptions.UserCanNotHandleEventException;
import io.github.externschool.planner.service.CourseService;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.StudyPlanService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import io.github.externschool.planner.util.CollatorHolder;
import io.github.externschool.planner.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT;
import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
import static io.github.externschool.planner.util.Constants.HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_NOT_DEFINED;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_SUBSCRIBE_SCHEDULE_EVENT_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_WEEK_WORKING_DAYS;

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
    private final ScheduleService scheduleService;
    private final ScheduleEventTypeService scheduleEventTypeService;
    private final EmailService emailService;

    @Autowired
    public StudentController(final StudentService studentService,
                             final PersonService personService,
                             final UserService userService,
                             final VerificationKeyService keyService,
                             final ConversionService conversionService,
                             final RoleService roleService,
                             final CourseService courseService,
                             final TeacherService teacherService,
                             final StudyPlanService planService,
                             final ScheduleService scheduleService,
                             final ScheduleEventTypeService scheduleEventTypeService,
                             final EmailService emailService) {
        this.studentService = studentService;
        this.personService = personService;
        this.userService = userService;
        this.keyService = keyService;
        this.conversionService = conversionService;
        this.roleService = roleService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.planService = planService;
        this.scheduleService = scheduleService;
        this.scheduleEventTypeService = scheduleEventTypeService;
        this.emailService = emailService;
    }

    /**
     * Displays List of All Students to Admin and Filtered By Id to Teacher
     * @param request ?search= request parameter
     * @param principal principal user
     * @return ModelAndView
     * */
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping("/")
    @SuppressWarnings("unchecked")
    public ModelAndView displayStudentListToTeacher(@RequestParam(value = "search", required = false) String request,
                                                    Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        if (isTeacher(user)) {
            Long id = Optional.ofNullable(user)
                    .map(User::getVerificationKey)
                    .map(VerificationKey::getPerson)
                    .map(Person::getId)
                    .orElse(null);
            return prepareStudentListModelAndView(id, 0);
        }

        ModelAndView modelAndView = prepareStudentListModelAndView(null, 0);
        if (request != null) {
            modelAndView.addObject("students",
                    Utils.searchRequestFilter((List<StudentDTO>)(modelAndView.getModel().get("students")), request));
        }

        return modelAndView;
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
        if (student == null) {
            return redirectByRole(principal);
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

    @Secured("ROLE_STUDENT")
    @GetMapping("/profile")
    public ModelAndView displayStudentProfileToStudent(final Principal principal) {
        Long id = userService.getUserByEmail(principal.getName()).getVerificationKey().getPerson().getId();
        StudentDTO studentDTO = conversionService.convert(studentService.findStudentById(id), StudentDTO.class);

        return showStudentProfileForm(studentDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayStudentProfileToEdit(@PathVariable("id") Long id) {
        StudentDTO studentDTO = conversionService.convert(studentService.findStudentById(id), StudentDTO.class);

        return showStudentProfileForm(studentDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayStudentProfileToAdd() {
        return showStudentProfileForm(new StudentDTO(), true);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormStudentProfileActionSave(@ModelAttribute("student") @Valid StudentDTO studentDTO,
                                                            BindingResult bindingResult,
                                                            ModelMap model,
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

        studentDTO.setVerificationKey(keyService.saveOrUpdateKey(Optional.ofNullable(studentDTO.getVerificationKey())
                .orElse(new VerificationKey())));
        Student student = studentService.saveOrUpdateStudent(conversionService.convert(studentDTO, Student.class));
        if (isPrincipalAnAdmin(principal)) {
            Optional<User> user = Optional.ofNullable(student)
                    .map(Student::getVerificationKey)
                    .map(VerificationKey::getUser)
                    .map(u -> userService.save(userService.assignNewRolesByKey(u, u.getVerificationKey())));
            if (!user.isPresent()) {
                Optional.ofNullable(student).ifPresent(t ->
                        userService.createAndSaveFakeUserWithKeyAndRoleName(t.getVerificationKey(),
                                "ROLE_STUDENT"));
            }
        }

        return new ModelAndView("redirect:/student/" + student.getId() + "/plan", model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @GetMapping(value = "/cancel")
    public ModelAndView processFormStudentProfileCancel( Principal principal) {
        return redirectByRole(principal);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView processStudentListFormDelete(@PathVariable("id") Long id) {
        //TODO Add deletion confirmation
        Optional.ofNullable(id).map(studentService::findStudentById)
                .map(Student::getVerificationKey)
                .map(VerificationKey::getUser)
                .ifPresent(user -> {
                    if (emailService.emailIsValid(user.getEmail())) {
                        userService.assignNewRolesByKey(user, keyService.saveOrUpdateKey(new VerificationKey()));
                    } else {
                        userService.deleteUser(user);
                    }
                });
        studentService.deleteStudentById(id);

        return new ModelAndView("redirect:/student/");
    }

    /**
     * Assigns new Verification Key to the Student whose Id provided
     * When key change confirmed:
     *         DTO Receives a NEW KEY which is instantly assigned,
     *         an old key is removed from user (if present),
     *         unbound user is DELETED from the database,
     *         and a NEW one created for the key recently assigned to DTO
     *
     * @param id Student Id
     * @return ModelAndView
     */
    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{id}/new-key")
    public ModelAndView processStudentProfileFormActionNewKey(@PathVariable("id") Long id) {
        StudentDTO studentDTO = Optional.ofNullable(studentService.findStudentById(id))
                .map(student -> conversionService.convert(student, StudentDTO.class))
                .map(s -> {
                    Optional.ofNullable(s.getVerificationKey())
                            .map(VerificationKey::getUser)
                            .ifPresent(userService::deleteUser);
                    StudentDTO dto = (StudentDTO)keyService.setNewKeyToDTO(s);
                    userService.createAndSaveFakeUserWithKeyAndRoleName(dto.getVerificationKey(),
                            "ROLE_STUDENT");
                    return dto;
                })
                .orElse(new StudentDTO());

        Optional.ofNullable(userService.getUserByEmail(studentDTO.getEmail()))
                .ifPresent(user -> {
                    userService.createNewKeyWithNewPersonAndAddToUser(user);
                    userService.save(user);
                });

        return showStudentProfileForm(studentDTO, true);
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

    @Secured("ROLE_STUDENT")
    @GetMapping("/subscriptions")
    public ModelAndView displaySubscriptionsToStudent(final ModelMap model, final Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        return prepareSubscriptionsModelAndView(user, model);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/subscriptions")
    public ModelAndView displaySubscriptionsToAdmin(@PathVariable("gid") Long studentId, final ModelMap model) {
        User user = Optional.ofNullable(personService.findPersonById(studentId))
                .map(Person::getVerificationKey)
                .map(VerificationKey::getUser)
                .orElse(null);

        return prepareSubscriptionsModelAndView(user, model);
    }

    @Secured("ROLE_STUDENT")
    @GetMapping("/teacher/schedule")
    public ModelAndView displayTeachersListToStudent(final ModelMap model, final Principal principal) {
        Long studentId = Optional.ofNullable(userService.getUserByEmail(principal.getName()))
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .map(Person::getId)
                .orElse(null);

        return prepareScheduleModelAndView(studentId, null, model);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/teacher/schedule")
    public ModelAndView displayTeachersListToAdmin(@PathVariable("gid") Long studentId, final ModelMap model) {

        return prepareScheduleModelAndView(studentId, null, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @GetMapping("/{gid}/teacher/{id}/schedule")
    public ModelAndView displayTeacherSchedule(@PathVariable("gid") Long studentId,
                                                @PathVariable("id") Long teacherId,
                                                final ModelMap model) {

        return prepareScheduleModelAndView(studentId, teacherId, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @GetMapping("/{gid}/teacher/{id}/event/{event}/subscribe")
    public ModelAndView displaySubscriptionModal(@PathVariable("gid") Long studentId,
                                                 @PathVariable("id") Long teacherId,
                                                 @PathVariable("event") Long eventId,
                                                 ModelMap model) {
        ModelAndView modelAndView = prepareScheduleModelAndView(studentId, teacherId, model);
        modelAndView.addObject("event",
                conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("student/student_schedule :: subscribeEvent");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @PostMapping("/{gid}/teacher/{id}/event/{event}/subscribe")
    public ModelAndView processSubscriptionModal(@PathVariable("gid") Long studentId,
                                                 @PathVariable("id") Long teacherId,
                                                 @PathVariable("event") Long eventId,
                                                 ModelMap model) {
        ModelAndView modelAndView = new ModelAndView(
                "redirect:/student/" + studentId + "/teacher/" + teacherId + "/schedule", model);
        try {
            subscribeScheduleEvent(studentId, eventId);
        } catch (UserCanNotHandleEventException e) {
            modelAndView = prepareScheduleModelAndView(studentId, teacherId, model);
            modelAndView.addObject("error", e.getMessage());
        }

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @GetMapping("/{gid}/teacher/{id}/event/{event}/unsubscribe")
    public ModelAndView displayUnsubscribeModal(@PathVariable("gid") Long studentId,
                                                @PathVariable("id") Long teacherId,
                                                @PathVariable("event") Long eventId,
                                                ModelMap model,
                                                final Principal principal) {
        ModelAndView modelAndView = prepareScheduleModelAndView(studentId, teacherId, model);
        modelAndView.addObject("event",
                conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("student/student_schedule :: unsubscribe");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
    @PostMapping("/{gid}/teacher/{id}/event/{event}/unsubscribe")
    public ModelAndView processUnsubscribeModal(@PathVariable("gid") Long studentId,
                                                @PathVariable("id") Long teacherId,
                                                @PathVariable("event") Long eventId,
                                                ModelMap model) {
        ModelAndView modelAndView = new ModelAndView(
                "redirect:/student/" + studentId + "/teacher/" + teacherId + "/schedule", model);
        try {
            unsubscribeScheduleEvent(studentId, eventId);
        } catch (UserCanNotHandleEventException e) {
            modelAndView = prepareScheduleModelAndView(studentId, teacherId, model);
            modelAndView.addObject("error", e.getMessage());
        }

        return modelAndView;
    }

    // TODO Simplify
    private ModelAndView prepareScheduleModelAndView(final Long studentId, final Long teacherId, final ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("student/student_schedule", model);

        // TODO Add invalid studentId error checking
        LocalDate currentWeekFirstDay = scheduleService.getCurrentWeekFirstDay();
        LocalDate nextWeekFirstDay = scheduleService.getNextWeekFirstDay();
        List<LocalDate> currentWeekDates = scheduleService.getWeekStartingFirstDay(currentWeekFirstDay);
        List<LocalDate> nextWeekDates = scheduleService.getWeekStartingFirstDay(nextWeekFirstDay);
        List<List<ScheduleEvent>> currentWeekEvents = new ArrayList<>();
        List<List<ScheduleEvent>> nextWeekEvents = new ArrayList<>();
        TeacherDTO teacherTeacher = new TeacherDTO();
        PersonDTO studentPerson = conversionService.convert(personService.findPersonById(studentId), PersonDTO.class);
        Optional<ScheduleEvent> subscribedEvent = Optional.empty();
        Optional<LocalDateTime> mostRecentUpdate = Optional.empty();
        long incomingEventsNumber = 0;

        Optional<User> optionalTeacherUser = getOptionalTeacherUser(teacherId);
        Optional<User> optionalStudentUser = getOptionalStudentUser(studentId);
        if (teacherId != null
                && optionalStudentUser.isPresent()
                && optionalTeacherUser.isPresent()) {
            User teacherUser = optionalTeacherUser.get();
            User studentUser = optionalStudentUser.get();
            teacherTeacher = conversionService.convert(teacherService.findTeacherById(teacherId), TeacherDTO.class);

            // when user has any subscribed event no more events available to subscribe are shown
            List<ScheduleEvent> allCurrentEvents = scheduleService.getEventsByOwnerStartingBetweenDates(
                    teacherUser,
                    LocalDate.now(),
                    currentWeekFirstDay.plusDays(14));
            subscribedEvent = allCurrentEvents.stream()
                    .map(ScheduleEvent::getParticipants)
                    .flatMap(Set::stream)
                    .filter(participant -> participant.getUser().equals(studentUser))
                    .findFirst()
                    .map(Participant::getEvent);
            if (subscribedEvent.isPresent()) {
                ScheduleEvent singleEvent = subscribedEvent.get();
                addByDatesSingletonListToEventsListOfLists(currentWeekDates, currentWeekEvents, singleEvent);
                addByDatesSingletonListToEventsListOfLists(nextWeekDates, nextWeekEvents, singleEvent);
            } else {
                currentWeekDates.forEach(date -> currentWeekEvents.add(getEventsAvailableToStudent(teacherUser, date)));
                nextWeekDates.forEach(date -> nextWeekEvents.add(getEventsAvailableToStudent(teacherUser, date)));
            }
            List<ScheduleEvent> incomingEvents = filterEventsAvailableToStudent(
                    studentUser,
                    scheduleService.getEventsByOwnerStartingBetweenDates(
                            teacherUser,
                            currentWeekFirstDay,
                            currentWeekFirstDay.plusDays(13)));
            mostRecentUpdate = incomingEvents.stream()
                    .map(ScheduleEvent::getModifiedAt)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder());
            incomingEventsNumber = incomingEvents.stream()
                    .filter(event -> !event.isCancelled() && event.isOpen())
                    .count();
        } else {
            currentWeekDates.forEach(date -> currentWeekEvents.add(new ArrayList<>()));
            nextWeekDates.forEach(date -> nextWeekEvents.add(new ArrayList<>()));
        }
        List<TeacherDTO> teachers = new ArrayList<>();
        courseService.findAllByStudentId(studentId)
                .forEach(course -> {
                    Teacher teacher = course.getTeacher();
                    if(!teacher.getLastName().equals(UK_COURSE_NO_TEACHER)) {
                        Optional<TeacherDTO> current = teachers.stream()
                                .filter(teacherDTO -> teacherDTO.getId().equals(teacher.getId()))
                                .findFirst();
                        if (current.isPresent()) {
                            current.get().setOptionalData(current.get().getOptionalData() + ", " + course.getTitle());
                        } else {
                            Optional.ofNullable(conversionService.convert(teacher, TeacherDTO.class)).ifPresent(dto -> {
                                dto.setOptionalData(course.getTitle());
                                teachers.add(dto);
                            });
                        }
                    }
                });

        modelAndView.addObject("student", studentPerson);
        modelAndView.addObject("teacher", teacherTeacher);
        modelAndView.addObject("teachers", teachers);
        modelAndView.addObject("weekDays", UK_WEEK_WORKING_DAYS);
        modelAndView.addObject("currentWeek", currentWeekDates);
        modelAndView.addObject("nextWeek", nextWeekDates);
        modelAndView.addObject("currentWeekEvents", convertListOfListsToDTO(currentWeekEvents));
        modelAndView.addObject("nextWeekEvents", convertListOfListsToDTO(nextWeekEvents));
        modelAndView.addObject("recentUpdate", mostRecentUpdate.orElse(null));
        modelAndView.addObject("availableEvents", incomingEventsNumber);
        modelAndView.addObject("event",
                subscribedEvent
                        .map(event -> conversionService.convert(event, ScheduleEventDTO.class))
                        .orElse(ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                                .withDate(FIRST_MONDAY_OF_EPOCH)
                                .withStartTime(LocalTime.MIN)
                                .withEventType(UK_EVENT_TYPE_NOT_DEFINED)
                                .withDescription(UK_EVENT_TYPE_NOT_DEFINED)
                                .withTitle(UK_EVENT_TYPE_NOT_DEFINED)
                                .withCreated(LocalDateTime.now())
                                .withIsOpen(true)
                                .build()));

        return modelAndView;
    }


    private ModelAndView prepareSubscriptionsModelAndView(final User user, final ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("student/student_subscriptions", model);
        modelAndView.addObject("student", Optional.ofNullable(user)
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .orElse(null));
        modelAndView.addObject("participants", Optional.ofNullable(user)
                .map(u -> scheduleService.getParticipantsByUser(u).stream()
                        .map(p -> conversionService.convert(p, ParticipantDTO.class))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>()));

        return modelAndView;
    }

    private void addByDatesSingletonListToEventsListOfLists(final List<LocalDate> dates,
                                                            final List<List<ScheduleEvent>> events,
                                                            final ScheduleEvent singletonEvent) {
        dates.forEach(date ->
                events.add(date.isEqual(singletonEvent.getStartOfEvent().toLocalDate())
                        ? Collections.singletonList(singletonEvent)
                        : Collections.emptyList()));
    }

    private List<List<ScheduleEventDTO>> convertListOfListsToDTO(final List<List<ScheduleEvent>> list) {
        return list.stream()
                .map(l -> l.stream()
                        .map(event -> conversionService.convert(event, ScheduleEventDTO.class))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<ScheduleEvent> filterEventsAvailableToStudent(final User student, final List<ScheduleEvent> events) {
        Role role = roleService.getRoleByName("ROLE_STUDENT");
        List<ScheduleEventType> availableTypes = scheduleEventTypeService.loadEventTypes().stream()
                .filter(type -> type.getParticipants().contains(role))
                .collect(Collectors.toList());

        return events.stream()
                .filter(event -> availableTypes.contains(event.getType()))
                .filter(ScheduleEvent::isOpen)
                .filter(event -> event.getStartOfEvent().isAfter(LocalDateTime.now()
                        // min date and time before new appointments
                        .plus(DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT)
                        .plus(HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT)))
                .collect(Collectors.toList());
    }

    private List<ScheduleEvent> getEventsAvailableToStudent(User user, LocalDate date) {

        return filterEventsAvailableToStudent(user, scheduleService.getNonCancelledEventsByOwnerAndDate(user, date));
    }

    //TODO Refactor since GuestController has identical method. Probably move to service
    private void subscribeScheduleEvent(Long studentId, Long eventId) throws UserCanNotHandleEventException {
        User user = Optional.ofNullable(personService.findPersonById(studentId))
                .map(Person::getVerificationKey)
                .map(VerificationKey::getUser)
                .orElse(null);
        Optional<Participant> participant = scheduleService.addParticipant(user, scheduleService.getEventById(eventId));
        if (!participant.isPresent()) {
            throw new UserCanNotHandleEventException(UK_SUBSCRIBE_SCHEDULE_EVENT_ERROR_MESSAGE);
        }
    }

    //TODO same as previous - Probably move to Service
    private void unsubscribeScheduleEvent(Long studentId, Long eventId) throws UserCanNotHandleEventException {
        User user = Optional.ofNullable(personService.findPersonById(studentId))
                .map(Person::getVerificationKey)
                .map(VerificationKey::getUser)
                .orElse(null);
        ScheduleEvent event = scheduleService.getEventById(eventId);
        Optional<Participant> participant = scheduleService.findParticipantByUserAndEvent(user, event);
        if (!participant.isPresent()) {
            throw new UserCanNotHandleEventException(UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE);
        }
        scheduleService.removeParticipant(participant.get());
        scheduleService.findEventByIdSetOpenByStateAndSave(eventId, true);
    }


    private Optional<User> getOptionalTeacherUser(final Long id) {
        return Optional.ofNullable(teacherService.findTeacherById(id))
                .flatMap(teacher -> Optional.ofNullable(teacher.getVerificationKey()).map(VerificationKey::getUser));
    }

    private Optional<User> getOptionalStudentUser(final Long id) {
        return Optional.ofNullable(personService.findPersonById(id))
                .flatMap(student -> Optional.ofNullable(student.getVerificationKey()).map(VerificationKey::getUser));
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

    private ModelAndView redirectByRole(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
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

    //TODO Move to User Service
    private Boolean isUserAnAdmin(User user) {
        return Optional.ofNullable(user)
                .map(User::getRoles)
                .map(roles -> roles.contains(roleService.getRoleByName("ROLE_ADMIN")))
                .orElse(Boolean.FALSE);
    }

    private Boolean isPrincipalAnAdmin(Principal principal) {
        return Optional.ofNullable(principal)
                .map(p -> userService.getUserByEmail(p.getName()))
                .map(this::isUserAnAdmin)
                .orElse(false);
    }
}
