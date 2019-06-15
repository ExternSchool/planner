package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.service.CourseService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
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

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.externschool.planner.util.Constants.DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE;
import static io.github.externschool.planner.util.Constants.DEFAULT_TIME_WHEN_WORKING_DAY_BEGINS;
import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
import static io.github.externschool.planner.util.Constants.UK_COURSE_ADMIN_IN_CHARGE;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_NOT_DEFINED;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_NO_EVENT_TYPE_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_WEEK_WORKING_DAYS;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final SchoolSubjectService subjectService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final UserService userService;
    private final RoleService roleService;
    private final ScheduleService scheduleService;
    private final ScheduleEventTypeService typeService;
    private final EmailService emailService;
    private final CourseService courseService;

    @Autowired
    public TeacherController(final TeacherService teacherService,
                             final SchoolSubjectService subjectService,
                             final ConversionService conversionService,
                             final VerificationKeyService keyService,
                             final UserService userService,
                             final RoleService roleService,
                             final ScheduleService scheduleService,
                             final ScheduleEventTypeService typeService,
                             final EmailService emailService,
                             final CourseService courseService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
        this.scheduleService = scheduleService;
        this.typeService = typeService;
        this.emailService = emailService;
        this.courseService = courseService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/")
    public ModelAndView displayTeacherList(@RequestParam(value = "search", required = false) String request) {
        List<Teacher> teachers = teacherService.findAllByOrderByLastName();
        Optional.ofNullable(teacherService.findAllByLastName(UK_COURSE_NO_TEACHER))
                .ifPresent(t -> t.forEach(teachers::remove));
        List<TeacherDTO> teacherDTOs = teachers.stream()
                .filter(Objects::nonNull)
                .map(teacher -> conversionService.convert(teacher, TeacherDTO.class))
                .collect(Collectors.toList());
        ModelAndView modelAndView =
                new ModelAndView("teacher/teacher_list", "teachers", teacherDTOs);
        if (request != null) {
            modelAndView.addObject("teachers", Utils.searchRequestFilter(teacherDTOs, request));
        }

        return modelAndView;
    }

    /**
     * Searches teacher by id, and redirects to teachers list, completes search by the Last Name if teacher is found
     * @param id teacher's id
     * @param principal principal user
     * @return ModelAndView
     */
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/search/{id}")
    public ModelAndView displayTeacherWithSearch(@PathVariable(value = "id", required = false) Long id,
                                                 Principal principal) {
        Teacher teacher = teacherService.findTeacherById(id);
        if (teacher != null) {
            return displayTeacherList(teacher.getLastName());
        }

        return redirectByRole(principal);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/profile")
    public ModelAndView displayTeacherProfileToTeacher(final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);

        Long id = userService.getUserByEmail(principal.getName()).getVerificationKey().getPerson().getId();
        Teacher teacher = Optional.ofNullable(id).map(teacherService::findTeacherById).orElse(null);
        if (teacher != null) {
            TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);
            modelAndView = displayTeacherProfileForm(teacherDTO, false);
        }

        return modelAndView;
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/visitors")
    public ModelAndView displayTeacherVisitorsToTeacher(final Principal principal) {
        final User user = userService.getUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();

        return new ModelAndView("redirect:/teacher/" + id + "/visitors");
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/visitors")
    public ModelAndView displayTeacherVisitors(final @PathVariable("id") Long id,
                                               final @RequestParam(value="start",required=false) LocalDate historyStart,
                                               final @RequestParam(value="end",required=false) LocalDate historyEnd,
                                               final @RequestParam(value="search",required=false) String searchFrag,
                                               final @RequestParam(value="cancelled",required=false) Integer cancelled,
                                               final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            LocalDate start = historyStart != null ? historyStart : LocalDate.now();
            LocalDate end = historyEnd != null ? historyEnd : scheduleService.getNextWeekFirstDay().plusDays(6);
            String search = searchFrag != null ? searchFrag : "";
            boolean showCancelled = cancelled != null && cancelled.equals(1);
            if (end.isBefore(start)) {
                LocalDate temp = start;
                start = end;
                end = temp;
            }
            modelAndView = prepareVisitorsList(user, id, start, end, search, showCancelled);
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayTeacherProfileToEdit(@PathVariable("id") Long id, final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);

        Teacher teacher = Optional.ofNullable(id).map(teacherService::findTeacherById).orElse(null);
        if(teacher != null) {
            TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);
            modelAndView = displayTeacherProfileForm(teacherDTO, false);
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayTeacherProfileToAdd() {
        return displayTeacherProfileForm(new TeacherDTO(), true);
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processTeacherProfileFormSave(@ModelAttribute("teacher") TeacherDTO teacherDTO,
                                                      final Principal principal) {
        teacherDTO.setVerificationKey(keyService.saveOrUpdateKey(Optional.ofNullable(teacherDTO.getVerificationKey())
                .orElse(new VerificationKey())));
        Teacher teacher = teacherService.saveOrUpdateTeacher(conversionService.convert(teacherDTO, Teacher.class));
        if (isPrincipalAnAdmin(principal)) {
            Optional<User> user = Optional.ofNullable(teacher)
                    .map(Teacher::getVerificationKey)
                    .map(VerificationKey::getUser)
                    .map(u -> userService.save(userService.assignNewRolesByKey(u, u.getVerificationKey())));
            if (!user.isPresent()) {
                Optional.ofNullable(teacher).ifPresent(t -> {
                    User newUser = userService.createAndSaveFakeUserWithKeyAndRoleName(t.getVerificationKey(),
                            "ROLE_TEACHER");
                    if (t.getOfficial() != null && !t.getOfficial().isEmpty()) {
                        newUser.addRole(roleService.getRoleByName("ROLE_OFFICER"));
                    }
                });
            }
        }

        return redirectByRole(principal);
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping(value = "/update") // TODO change to /cancel
    public ModelAndView processTeacherProfileFormCancel(final Principal principal) {
        return redirectByRole(principal);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/delete-modal")
    public ModelAndView displayTeacherListFormDeleteModal(@PathVariable("id") Long id,
                                                          final ModelMap model) {
        TeacherDTO teacher = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);
        if (teacher != null && teacher.getLastName().equals(UK_COURSE_ADMIN_IN_CHARGE)) {
            teacher = null;
        }
        model.addAttribute("teacher", teacher);

        return new ModelAndView("teacher/teacher_list :: deleteTeacher", model);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView processTeacherListFormDelete(@PathVariable("id") Long id) {
        Optional.ofNullable(id).map(teacherService::findTeacherById)
                .map(Teacher::getVerificationKey)
                .map(VerificationKey::getUser)
                .ifPresent(user -> {
                    if (emailService.emailIsValid(user.getEmail())) {
                        userService.assignNewRolesByKey(user, keyService.saveOrUpdateKey(new VerificationKey()));
                    } else {
                        userService.deleteUser(user);
                    }
                });
        teacherService.deleteTeacherById(id);

        return new ModelAndView("redirect:/teacher/");
    }

    /**
     * Assigns new Verification Key to selected Teacher's profile.
     *  When key change confirmed:
     *      DTO Receives a NEW KEY which is instantly assigned, an old key is removed from user (if present),
     *      the old user is DELETED from database, and a NEW one created for the key recently assigned to DTO.
     *
     * @param id Teacher's profile id
     * @param principal Principal user
     * @return Model and View to display TeacherProfileForm
     */
    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{id}/new-key")
    public ModelAndView processTeacherProfileFormActionNewKey(@PathVariable("id") Long id, Principal principal) {
        TeacherDTO teacherDTO = Optional.ofNullable(teacherService.findTeacherById(id))
                .map(teacher -> conversionService.convert(teacher, TeacherDTO.class))
                .map(t -> {
                    Optional.ofNullable(t.getVerificationKey())
                            .map(VerificationKey::getUser)
                            .ifPresent(userService::deleteUser);
                    TeacherDTO dto = (TeacherDTO)keyService.setNewKeyToDTO(t);
                    userService.createAndSaveFakeUserWithKeyAndRoleName(dto.getVerificationKey(),
                            "ROLE_TEACHER");
                    return dto;
                })
                .orElse(new TeacherDTO());

        Optional.ofNullable(userService.getUserByEmail(teacherDTO.getEmail()))
                .ifPresent(user -> {
                    userService.createNewKeyWithNewPersonAndAddToUser(user);
                    userService.save(user);
                });

        return displayTeacherProfileForm(teacherDTO, true);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{id}/admin")
    public ModelAndView processTeacherProfileFormActionAdmin(@PathVariable("id") Long id,
                                                                Principal principal) {
        if (id == null) {
            return redirectByRole(principal);
        }
        Teacher teacher = teacherService.findTeacherById(id);
        Optional.ofNullable(teacher.getVerificationKey())
                .map(VerificationKey::getUser)
                .ifPresent(user -> {
                    Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
                    if (user.getRoles().contains(roleAdmin)) {
                        user.removeRole(roleAdmin);
                    } else {
                        user.addRole(roleAdmin);
                    }
                });
        TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);

        return displayTeacherProfileForm(teacherDTO, false);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/schedule")
    public ModelAndView displayTeacherScheduleToTeacher(final Principal principal) {
        final User user = userService.getUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();

        return new ModelAndView("redirect:/teacher/" + id + "/schedule");
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/schedule")
    public ModelAndView displayTeacherSchedule(@PathVariable("id") Long id,
                                               ModelMap model,
                                               final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

            LocalDate currentWeekFirstDay = scheduleService.getCurrentWeekFirstDay();
            LocalDate nextWeekFirstDay = scheduleService.getNextWeekFirstDay();
            List<LocalDate> currentWeekDates = scheduleService.getWeekStartingFirstDay(currentWeekFirstDay);
            List<LocalDate> nextWeekDates = scheduleService.getWeekStartingFirstDay(nextWeekFirstDay);

            List<List<ScheduleEventDTO>> currentWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> nextWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> standardWeekEvents = new ArrayList<>();

            currentWeekDates.forEach(date ->
                    currentWeekEvents.add(
                            addDescriptionAndConvertToDTO(
                                    scheduleService.getEventsByOwnerStartingBetweenDates(user, date, date))));
            nextWeekDates.forEach(date ->
                    nextWeekEvents.add(
                            addDescriptionAndConvertToDTO(
                                    scheduleService.getEventsByOwnerStartingBetweenDates(user, date, date))));
            List<ScheduleEvent> templateEvents = scheduleService.getDailyTemplateEventsByOwner(user);
            IntStream.rangeClosed(1, 5).mapToObj(DayOfWeek::of).forEach(day ->
                    standardWeekEvents.add(
                            addDescriptionAndConvertToDTO(templateEvents.stream()
                                    .filter(event -> event.getStartOfEvent().getDayOfWeek().equals(day))
                                    .collect(Collectors.toList()))));

            List<ScheduleEvent> incomingEvents = scheduleService.getEventsByOwnerStartingBetweenDates(
                    user,
                    currentWeekFirstDay,
                    currentWeekFirstDay.plusDays(13));
            Optional<LocalDateTime> mostRecentUpdate = incomingEvents.stream()
                    .map(ScheduleEvent::getModifiedAt)
                    .max(Comparator.naturalOrder());
            long incomingEventsNumber = incomingEvents.stream()
                    .filter(event -> !event.isCancelled() && event.isOpen())
                    .count();
            modelAndView = new ModelAndView("teacher/teacher_schedule", model);
            modelAndView.addObject("teacher", teacherDTO);
            modelAndView.addObject("weekDays", UK_WEEK_WORKING_DAYS);
            modelAndView.addObject("currentWeek", currentWeekDates);
            modelAndView.addObject("nextWeek", nextWeekDates);
            modelAndView.addObject("currentWeekEvents", currentWeekEvents);
            modelAndView.addObject("nextWeekEvents", nextWeekEvents);
            modelAndView.addObject("standardWeekEvents", standardWeekEvents);
            modelAndView.addObject("recentUpdate", mostRecentUpdate.orElse(null));
            modelAndView.addObject("availableEvents", incomingEventsNumber);
            modelAndView.addObject("newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO().build());
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/current-week/{day}")
    public ModelAndView displayTeacherCancelCurrentWeekDayModal(@PathVariable("id") Long id,
                                                                @PathVariable("day") int dayOfWeek,
                                                                ModelMap model,
                                                                final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            modelAndView = new ModelAndView("teacher/teacher_schedule :: cancelCurrentWeekDay", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping("/{id}/current-week/{day}/cancel")
    public ModelAndView processTeacherCancelCurrentWeekDay(@PathVariable("id") Long id,
                                                           @PathVariable("day") int dayOfWeek,
                                                           ModelMap model,
                                                           final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            List<ScheduleEvent> events = scheduleService.getNonCancelledEventsByOwnerAndDate(
                    optionalUser.get(),
                    scheduleService.getCurrentWeekFirstDay().plus(Period.ofDays(dayOfWeek)));

            scheduleService.cancelOrDeleteEventsAndMailToParticipants(events);

            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/new-current/{day}")
    public ModelAndView displayTeacherNewCurrentModal(@PathVariable("id") Long id,
                                                      @PathVariable("day") int dayOfWeek,
                                                      ModelMap model,
                                                      final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("eventTypes",
                    typeService.getAllEventTypesByUserRoles(user));
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            LocalDate thisDay = scheduleService.getCurrentWeekFirstDay().plusDays(dayOfWeek);

            List<ScheduleEvent> actualEvents = scheduleService.getNonCancelledEventsByOwnerAndDate(user, thisDay);
            Optional<ScheduleEvent> latestEvent = actualEvents.stream().reduce((first, second) -> second);
            LocalTime timeToStartNextEvent = latestEvent
                    .map(event -> Optional.ofNullable(event.getEndOfEvent())
                            .map(LocalDateTime::toLocalTime)
                            .orElse(Optional.ofNullable(event.getType())
                                    .map(ScheduleEventType::getDurationInMinutes)
                                    .map(min -> event.getStartOfEvent().toLocalTime().plusMinutes(min))
                                    .orElse(event.getStartOfEvent().toLocalTime()
                                            .plusMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE))))
                    .orElse(DEFAULT_TIME_WHEN_WORKING_DAY_BEGINS);
            model.addAttribute("thisDayEvents", addDescriptionAndConvertToDTO(actualEvents));
            model.addAttribute(
                    "newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                            .withDate(thisDay)
                            .withStartTime(timeToStartNextEvent)
                            .withEventType(latestEvent.map(ScheduleEvent::getType).toString())
                            .withDescription(latestEvent.map(ScheduleEvent::getDescription).orElse(""))
                            .build());

            modelAndView = new ModelAndView("teacher/teacher_schedule :: newCurrent", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/new-current/{day}/add")
    public ModelAndView processTeacherCurrentModalFormAddEvent(@PathVariable("id") Long id,
                                                               @PathVariable("day") int dayOfWeek,
                                                               @ModelAttribute("newEvent") ScheduleEventDTO eventDTO,
                                                               BindingResult bindingResult,
                                                               ModelMap model,
                                                               final Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
            if (eventDTO.getEventType() == null) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_NO_EVENT_TYPE_MESSAGE);
            }
        } catch (BindingResultException e) {
            return displayTeacherSchedule(id, model, principal).addObject("error", e.getMessage());
        }
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            Optional<ScheduleEventType> type = typeService.loadEventTypes().stream()
                    .filter(eventType -> eventType.getName().equals(eventDTO.getEventType()))
                    .findAny();
            Integer duration = type.map(ScheduleEventType::getDurationInMinutes)
                    .orElse(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            eventDTO.setTitle(type.map(ScheduleEventType::getName).orElse(UK_EVENT_TYPE_NOT_DEFINED));
            eventDTO.setDate(scheduleService.getCurrentWeekFirstDay().plusDays(dayOfWeek));
            scheduleService.createEventWithDuration(
                    optionalUser.get(),
                    eventDTO,
                    duration);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/next-week/{day}")
    public ModelAndView displayTeacherDeleteNextWeekDayModal(@PathVariable("id") Long id,
                                                             @PathVariable("day") int dayOfWeek,
                                                             ModelMap model,
                                                             final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            modelAndView = new ModelAndView("teacher/teacher_schedule :: cancelNextWeekDay", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping("/{id}/next-week/{day}/cancel")
    public ModelAndView processTeacherCancelNextWeekDay(@PathVariable("id") Long id,
                                                        @PathVariable("day") int dayOfWeek,
                                                        ModelMap model,
                                                        final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            List<ScheduleEvent> events = scheduleService.getNonCancelledEventsByOwnerAndDate(
                    optionalUser.get(),
                    scheduleService.getNextWeekFirstDay().plus(Period.ofDays(dayOfWeek)));

            scheduleService.cancelOrDeleteEventsAndMailToParticipants(events);

            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/new-next/{day}")
    public ModelAndView displayTeacherNewNextModal(@PathVariable("id") Long id,
                                                   @PathVariable("day") int dayOfWeek,
                                                   ModelMap model,
                                                   final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("eventTypes",
                    typeService.getAllEventTypesByUserRoles(user));
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            LocalDate thisDay = scheduleService.getNextWeekFirstDay().plusDays(dayOfWeek);

            List<ScheduleEvent> actualEvents = scheduleService.getNonCancelledEventsByOwnerAndDate(user, thisDay);
            Optional<ScheduleEvent> latestEvent = actualEvents.stream().reduce((first, second) -> second);
            LocalTime timeToStartNextEvent = latestEvent
                    .map(event -> Optional.ofNullable(event.getEndOfEvent())
                            .map(LocalDateTime::toLocalTime)
                            .orElse(Optional.ofNullable(event.getType())
                                    .map(ScheduleEventType::getDurationInMinutes)
                                    .map(min -> event.getStartOfEvent().toLocalTime().plusMinutes(min))
                                    .orElse(event.getStartOfEvent().toLocalTime()
                                            .plusMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE))))
                    .orElse(DEFAULT_TIME_WHEN_WORKING_DAY_BEGINS);
            model.addAttribute("thisDayEvents", addDescriptionAndConvertToDTO(actualEvents));
            model.addAttribute(
                    "newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                            .withDate(thisDay)
                            .withStartTime(timeToStartNextEvent)
                            .withEventType(latestEvent.map(ScheduleEvent::getType).toString())
                            .withDescription(latestEvent.map(ScheduleEvent::getDescription).orElse(""))
                            .build());

            modelAndView = new ModelAndView("teacher/teacher_schedule :: newNext", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/new-next/{day}/add")
    public ModelAndView processTeacherNextModalFormAddEvent(@PathVariable("id") Long id,
                                                            @PathVariable("day") int dayOfWeek,
                                                            @ModelAttribute("newEvent") ScheduleEventDTO eventDTO,
                                                            BindingResult bindingResult,
                                                            ModelMap model,
                                                            final Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
            if (eventDTO.getEventType() == null) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_NO_EVENT_TYPE_MESSAGE);
            }
        } catch (BindingResultException e) {
            return displayTeacherSchedule(id, model, principal).addObject("error", e.getMessage());
        }
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            Optional<ScheduleEventType> type = typeService.loadEventTypes().stream()
                    .filter(eventType -> eventType.getName().equals(eventDTO.getEventType()))
                    .findAny();
            Integer duration = type.map(ScheduleEventType::getDurationInMinutes)
                    .orElse(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            eventDTO.setTitle(type.map(ScheduleEventType::getName).orElse(UK_EVENT_TYPE_NOT_DEFINED));
            eventDTO.setDate(scheduleService.getNextWeekFirstDay().plusDays(dayOfWeek));
            scheduleService.createEventWithDuration(
                    optionalUser.get(),
                    eventDTO,
                    duration);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping(value = "/{id}/event/{eid}/modal")
    public ModelAndView displayModalFormDeleteEvent(@PathVariable("id") Long teacherId,
                                                    @PathVariable("eid") Long eventId,
                                                    ModelMap model,
                                                    final Principal principal) {
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_schedule :: deleteEvent", model);
        Optional<User> optionalUser = getOptionalUser(teacherId);
        ScheduleEvent event = scheduleService.getEventById(eventId);
        modelAndView.addObject(
                "teacher",
                conversionService.convert(teacherService.findTeacherById(teacherId), TeacherDTO.class));
        if (optionalUser.isPresent() && event != null && event.getParticipants().isEmpty()) {
            modelAndView.addObject("newEvent", conversionService.convert(event, ScheduleEventDTO.class));
        } else {
            modelAndView.addObject("newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO().build());
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/event/{eid}/delete")
    public ModelAndView processModalFormDeleteEvent(@PathVariable("id") Long teacherId,
                                                    @PathVariable("eid") Long eventId,
                                                    ModelMap modelMap,
                                                    final Principal principal) {
        Optional<User> optionalUser = getOptionalUser(teacherId);
        ScheduleEvent event = scheduleService.getEventById(eventId);
        if (optionalUser.isPresent() && event != null) {
            scheduleService.deleteEventById(eventId);
        }

        return new ModelAndView("redirect:/teacher/" + teacherId + "/schedule", modelMap);
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/day/{day}/modal-template")
    public ModelAndView displayTeacherModalTemplate(@PathVariable("id") Long id,
                                                    @PathVariable("day") int dayOfWeek,
                                                    ModelMap model,
                                                    final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("eventTypes", typeService.getAllEventTypesByUserRoles(user));
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            List<ScheduleEvent> actualEvents = scheduleService.getDailyTemplateEventsByOwner(user).stream()
                    .filter(event -> event.getStartOfEvent().getDayOfWeek().equals(DayOfWeek.of(dayOfWeek + 1)))
                    .collect(Collectors.toList());
            Optional<ScheduleEvent> latestEvent = actualEvents.stream().reduce((first, second) -> second);
            LocalDate dateToStartNextEvent = latestEvent
                    .map(event -> Optional.ofNullable(event.getEndOfEvent())
                            .map(LocalDateTime::toLocalDate)
                            .orElse(event.getStartOfEvent().toLocalDate()))
                    .orElse(FIRST_MONDAY_OF_EPOCH.plusDays(dayOfWeek));
            LocalTime timeToStartNextEvent = latestEvent
                    .map(event -> Optional.ofNullable(event.getEndOfEvent())
                            .map(LocalDateTime::toLocalTime)
                            .orElse(Optional.ofNullable(event.getType())
                                    .map(ScheduleEventType::getDurationInMinutes)
                                    .map(min -> event.getStartOfEvent().toLocalTime().plusMinutes(min))
                                    .orElse(event.getStartOfEvent().toLocalTime()
                                            .plusMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE))))
                    .orElse(DEFAULT_TIME_WHEN_WORKING_DAY_BEGINS);
            model.addAttribute("thisDayEvents", addDescriptionAndConvertToDTO(actualEvents));
            model.addAttribute(
                    "newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                            .withDate(dateToStartNextEvent)
                            .withStartTime(timeToStartNextEvent)
                            .withEventType(latestEvent.map(ScheduleEvent::getType).toString())
                            .withDescription(latestEvent.map(ScheduleEvent::getDescription).orElse(""))
                            .build());
            modelAndView = new ModelAndView("teacher/teacher_schedule :: newSchedule", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/day/{day}/add-template")
    public ModelAndView processTeacherScheduleModalFormAddTemplate(@PathVariable("id") Long id,
                                                                   @PathVariable("day") int dayOfWeek,
                                                                   @ModelAttribute("newEvent") ScheduleEventDTO eventDTO,
                                                                   BindingResult bindingResult,
                                                                   ModelMap model,
                                                                   final Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
            if (eventDTO.getEventType() == null) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_NO_EVENT_TYPE_MESSAGE);
            }
        } catch (BindingResultException e) {
            return displayTeacherSchedule(id, model, principal).addObject("error", e.getMessage());
        }
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            Optional<ScheduleEventType> type = typeService.loadEventTypes().stream()
                    .filter(eventType -> eventType.getName().equals(eventDTO.getEventType()))
                    .findAny();
            Integer duration = type.map(ScheduleEventType::getDurationInMinutes)
                    .orElse(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);

            eventDTO.setTitle(type.map(ScheduleEventType::getName).orElse(UK_EVENT_TYPE_NOT_DEFINED));
            ScheduleTemplate template = scheduleService.createTemplate(
                    optionalUser.get(),
                    eventDTO,
                    DayOfWeek.of(dayOfWeek + 1),
                    duration);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping(value = "/{id}/template/{eid}/delete-modal")
    public ModelAndView displayTeacherTemplateDeleteModal(@PathVariable("id") Long id,
                                                          @PathVariable("eid") Long templateId,
                                                          ModelMap model) {
        model.addAttribute(
                "teacher",
                conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
        scheduleService.findTemplateById(templateId).ifPresent(template ->
                model.addAttribute("newEvent", ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                        .withId(templateId)
                        .withTitle(template.getTitle())
                        .withDescription(template.getDescription())
                        .withStartTime(template.getStartOfEvent()).build()));

        return new ModelAndView("teacher/teacher_schedule :: deleteTemplate", model);
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/template/{eid}/delete")
    public ModelAndView processTeacherTemplateDelete(@PathVariable("id") Long id,
                                                     @PathVariable("eid") Long templateId,
                                                     ModelMap model,
                                                     final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        Optional<ScheduleTemplate> template = scheduleService.findTemplateById(templateId);
        if (optionalUser.isPresent() && template.isPresent()) {
            scheduleService.deleteTemplateById(templateId);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/template/publish")
    public ModelAndView processTeacherTemplatePublish(@PathVariable("id") Long id,
                                                      final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser.isPresent()) {
            scheduleService.recreateNextWeekEventsFromTemplatesForOwner(optionalUser.get());
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    private ModelAndView displayTeacherProfileForm(TeacherDTO teacherDTO, boolean isNew) {
        ModelAndView modelAndView =
                new ModelAndView("teacher/teacher_profile", "teacher", teacherDTO);
        modelAndView.addObject("isNew", isNew);
        modelAndView.addObject("isAdmin", isTeacherAnAdmin(teacherDTO));
        modelAndView.addObject("allSubjects", subjectService.findAllByOrderByTitle());

        return modelAndView;
    }

    private Boolean isNew(TeacherDTO teacherDTO) {
        return !Optional.ofNullable(teacherDTO)
                .map(t -> Optional.ofNullable(teacherService.findTeacherById(t.getId())).isPresent())
                .orElse(false);
    }

    private Boolean isPrincipalAnAdmin(Principal principal) {
        return Optional.ofNullable(principal)
                .map(p -> userService.getUserByEmail(p.getName()))
                .map(this::isUserAnAdmin)
                .orElse(false);
    }

    private Boolean isTeacherAnAdmin(TeacherDTO teacherDTO) {
        return Optional.ofNullable(teacherDTO.getId())
                .map(teacherService::findTeacherById)
                .map(Teacher::getVerificationKey)
                .map(VerificationKey::getUser)
                .map(this::isUserAnAdmin)
                .orElse(false);
    }

    //TODO Move to User Service
    private Boolean isUserAnAdmin(User user) {
        return Optional.ofNullable(user)
                .map(User::getRoles)
                .map(roles -> roles.contains(roleService.getRoleByName("ROLE_ADMIN")))
                .orElse(Boolean.FALSE);
    }
    private ModelAndView redirectByRole(Principal principal) {
        if (isPrincipalAnAdmin(principal)) {
            return new ModelAndView("redirect:/teacher/");
        }

        return new ModelAndView("redirect:/");
    }

    private Optional<User> getOptionalUser(final Long id) {
        return Optional.ofNullable(teacherService.findTeacherById(id))
                .map(teacher -> Optional.ofNullable(teacher.getVerificationKey()).map(VerificationKey::getUser))
                .orElse(Optional.of(new User()));
    }

    private String getEventDetails(Participant participant) {
        return Optional.ofNullable(participant.getEvent()).map(event -> {
            StringBuilder builder = new StringBuilder()
                    .append(event.getStartOfEvent().format(DateTimeFormatter.ofPattern("dd/MM HH:mm ")));
            // if event owner is an admin-in-charge, get test works details
            if (isParticipantAnAdminInCharge(event.getOwner())) {
                Optional.ofNullable(participant.getPlanOneId()).ifPresent(planId -> {
                    Course course = courseService.findCourseByStudentIdAndPlanId(
                            participant.getUser().getVerificationKey().getPerson().getId(),
                            planId);
                    builder.append(participant.getPlanOneSemesterOne()
                            ? "[" + course.getTitle() + " - " + course.getTeacher().getShortName() + ": семестр 1] "
                            : "");
                    builder.append(participant.getPlanOneSemesterTwo()
                            ? "[" + course.getTitle() + " - " + course.getTeacher().getShortName() + ": семестр 2] "
                            : "");
                });
                Optional.ofNullable(participant.getPlanTwoId()).ifPresent(planId -> {
                    Course course = courseService.findCourseByStudentIdAndPlanId(
                            participant.getUser().getVerificationKey().getPerson().getId(),
                            planId);
                    builder.append(participant.getPlanTwoSemesterOne()
                            ? "[" + course.getTitle() + " - " + course.getTeacher().getShortName() + ": семестр 1] "
                            : "");
                    builder.append(participant.getPlanTwoSemesterTwo()
                            ? "[" + course.getTitle() + " - " + course.getTeacher().getShortName() + ": семестр 2] "
                            : "");
                });
            } else {
                //or else event owner is a teacher so get event title
                builder.append(event.getTitle());
            }
            builder.append(event.getDescription().isEmpty() ? "" : ": ").append(event.getDescription());
            return builder.toString();
        }).orElse("");
    }

    private boolean isParticipantAnAdminInCharge(User user) {
        return Optional.ofNullable(user.getVerificationKey())
                .map(VerificationKey::getPerson)
                .map(Person::getLastName)
                .map(name -> name.equals(UK_COURSE_ADMIN_IN_CHARGE))
                .orElse(false);
    }

    private List<ScheduleEventDTO> addDescriptionAndConvertToDTO(final List<ScheduleEvent> events) {
        return events.stream()
                .map(event -> {
                    StringBuilder description = new StringBuilder(event.getDescription());
                    event.getParticipants().stream()
                            .limit(3)
                            .map(Participant::getUser)
                            .map(User::getVerificationKey)
                            .map(VerificationKey::getPerson)
                            .filter(Objects::nonNull)
                            .forEach(person -> {
                                description
                                        .append(description.length() > 0 ? ", " : "")
                                        .append(person.getShortName());
                                if(person instanceof Student) {
                                    GradeLevel gradeLevel = ((Student)person).getGradeLevel();
                                    description
                                            .append(", ")
                                            .append(gradeLevel.getValue())
                                            .append(" кл.");
                                }
                            });
                    ScheduleEventDTO eventDTO = conversionService.convert(event, ScheduleEventDTO.class);
                    Optional.ofNullable(eventDTO).ifPresent(dto -> dto.setDescription(description.toString()));
                    return eventDTO;
                })
                .collect(Collectors.toList());
    }

    private ModelAndView prepareVisitorsList(User user,
                                             Long teacherId,
                                             LocalDate start,
                                             LocalDate end,
                                             String search,
                                             boolean showCancelled) {
        Teacher teacher = teacherService.findTeacherById(teacherId);
        TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);
        List<StudentDTO> students = new ArrayList<>();
        List<PersonDTO> guests = new ArrayList<>();
        List<ScheduleEvent> sortedEvents = scheduleService.getEventsByOwnerStartingBetweenDates(user, start, end)
                .stream()
                .sorted(Comparator.comparing(ScheduleEvent::getStartOfEvent))
                .collect(Collectors.toList());
        for (ScheduleEvent event : sortedEvents) {
            if (!event.isCancelled() || showCancelled) {
                event.getParticipants().forEach(participant -> {
                    Optional.ofNullable(participant.getUser())
                            .map(User::getVerificationKey)
                            .map(VerificationKey::getPerson)
                            .filter(person -> person.getLastName().contains(search))
                            .ifPresent(person -> {
                                if (person instanceof Student) {
                                    Optional.ofNullable(conversionService.convert(person, StudentDTO.class))
                                            .ifPresent(studentDTO -> {
                                                studentDTO.setOptionalData(getEventDetails(participant));
                                                students.add(studentDTO);
                                            });
                                } else {
                                    Optional.ofNullable(conversionService.convert(person, PersonDTO.class))
                                            .ifPresent(guestDTO -> {
                                                guestDTO.setOptionalData(getEventDetails(participant));
                                                guests.add(guestDTO);
                                            });
                                }
                            });
                });
            }
        }
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_visitors",
                "teacher", teacherDTO);
        modelAndView.addObject("students", students);
        modelAndView.addObject("guests", guests);
        modelAndView.addObject("historyStart", LocalDate.now());
        modelAndView.addObject("historyEnd", scheduleService.getNextWeekFirstDay().plusDays(6));

        return modelAndView;
    }
}
