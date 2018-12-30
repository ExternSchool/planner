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
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.externschool.planner.util.Constants.DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE;
import static io.github.externschool.planner.util.Constants.DEFAULT_TIME_WHEN_WORKING_DAY_BEGINS;
import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_NOT_DEFINED;
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

    @Autowired
    public TeacherController(final TeacherService teacherService,
                             final SchoolSubjectService subjectService,
                             final ConversionService conversionService,
                             final VerificationKeyService keyService,
                             final UserService userService,
                             final RoleService roleService,
                             final ScheduleService scheduleService,
                             final ScheduleEventTypeService typeService,
                             final EmailService emailService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
        this.scheduleService = scheduleService;
        this.typeService = typeService;
        this.emailService = emailService;
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

    @Secured("ROLE_TEACHER")
    @GetMapping("/visitors")
    public ModelAndView displayTeacherVisitorsToTeacher(final Principal principal) {
        final User user = userService.getUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();

        return new ModelAndView("redirect:/teacher/" + id + "/visitors");
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/visitors")
    public ModelAndView displayTeacherVisitors(@PathVariable("id") Long id,
                                               final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);

        if (optionalUser != null && optionalUser.isPresent()) {
            User user = optionalUser.get();
            Teacher teacher = teacherService.findTeacherById(id);
            TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);
            List<StudentDTO> students = new ArrayList<>();
            List<PersonDTO> guests = new ArrayList<>();

            LocalDate start = LocalDate.now();
            LocalDate end = scheduleService.getNextWeekFirstDay().plusDays(6);
            List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(start, end))
                    .collect(Collectors.toList());

            for (LocalDate date : dates) {
                scheduleService.getNonCancelledEventsByOwnerAndDate(user, date).forEach(event -> {
                    event.getParticipants().stream()
                            .map(Participant::getUser)
                            .map(User::getVerificationKey)
                            .map(VerificationKey::getPerson)
                            .forEach(person -> {
                                if (person instanceof Student) {
                                    Optional.ofNullable(conversionService.convert(person, StudentDTO.class))
                                            .ifPresent(studentDTO -> {
                                                studentDTO.setOptionalData(getEventDetails(event));
                                                students.add(studentDTO);
                                            });
                                } else {
                                    Optional.ofNullable(conversionService.convert(person, PersonDTO.class))
                                            .ifPresent(guestDTO -> {
                                                guestDTO.setOptionalData(getEventDetails(event));
                                                guests.add(guestDTO);
                                            });
                                }
                            });
                });
            }
            modelAndView = new ModelAndView("teacher/teacher_visitors", "teacher", teacherDTO);
            modelAndView.addObject("students", students);
            modelAndView.addObject("guests", guests);
        }

        return modelAndView;
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
                                               final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);

        if (optionalUser != null && optionalUser.isPresent()) {
            User user = optionalUser.get();
            TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

            LocalDate currentWeekFirstDay = scheduleService.getCurrentWeekFirstDay();
            List<LocalDate> currentWeekDates = scheduleService.getWeekStartingFirstDay(currentWeekFirstDay);
            List<LocalDate> nextWeekDates = scheduleService.getWeekStartingFirstDay(scheduleService.getNextWeekFirstDay());
            // standard week schedule has no real date to start, so FIRST_MONDAY_OF_EPOCH is used
            List<LocalDate> standardWeek = scheduleService.getWeekStartingFirstDay(FIRST_MONDAY_OF_EPOCH);
            List<List<ScheduleEventDTO>> currentWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> nextWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> standardWeekEvents = new ArrayList<>();

            currentWeekDates.forEach(date ->
                    currentWeekEvents.add(
                            addDescriptionAndConvertToDTO(scheduleService.getNonCancelledEventsByOwnerAndDate(user, date))));
            nextWeekDates.forEach(date ->
                    nextWeekEvents.add(
                            addDescriptionAndConvertToDTO(scheduleService.getNonCancelledEventsByOwnerAndDate(user, date))));
            standardWeek.forEach(date ->
                    standardWeekEvents.add(
                            addDescriptionAndConvertToDTO(scheduleService.getNonCancelledEventsByOwnerAndDate(user, date))));

            List<ScheduleEvent> incomingEvents = scheduleService.getEventsByOwnerStartingBetweenDates(
                            user,
                            currentWeekFirstDay,
                            currentWeekFirstDay.plusDays(14));
            Optional<LocalDateTime> mostRecentUpdate = incomingEvents.stream()
                    .map(ScheduleEvent::getModifiedAt)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder());
            long incomingEventsNumber = incomingEvents.stream().filter(event -> !event.isCancelled()).count();

            modelAndView = new ModelAndView("teacher/teacher_schedule", "teacher", teacherDTO);
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
    @PostMapping("/{id}/event/{eid}/delete")
    public ModelAndView processTeacherEventDelete(@PathVariable("id") Long id,
                                                  @PathVariable("eid") Long eventId,
                                                  ModelMap model,
                                                  final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        ScheduleEvent event = scheduleService.getEventById(eventId);
        if (optionalUser.isPresent() && event != null) {
            scheduleService.deleteEventById(eventId);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/current-week/{day}")
    public ModelAndView displayTeacherDeleteCurrentWeekDayModal(@PathVariable("id") Long id,
                                                                @PathVariable("day") int dayOfWeek,
                                                                ModelMap model,
                                                                final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent()) {
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
    public ModelAndView processTeacherDeleteCurrentWeekDay(@PathVariable("id") Long id,
                                                           @PathVariable("day") int dayOfWeek,
                                                           ModelMap model,
                                                           final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent() && isWorkingDay(dayOfWeek)) {
            List<ScheduleEvent> events = scheduleService.getNonCancelledEventsByOwnerAndDate(
                    optionalUser.get(),
                    scheduleService.getCurrentWeekFirstDay().plus(Period.ofDays(dayOfWeek)));

            scheduleService.cancelEventsAndMailToParticipants(events);

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
        if (optionalUser != null && optionalUser.isPresent()) {
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
    public ModelAndView processTeacherDeleteNextWeekDay(@PathVariable("id") Long id,
                                                        @PathVariable("day") int dayOfWeek,
                                                        ModelMap model,
                                                        final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent() && isWorkingDay(dayOfWeek)) {
            List<ScheduleEvent> events = scheduleService.getNonCancelledEventsByOwnerAndDate(
                    optionalUser.get(),
                    scheduleService.getNextWeekFirstDay().plus(Period.ofDays(dayOfWeek)));

            scheduleService.cancelEventsAndMailToParticipants(events);

            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/new-schedule/{day}")
    public ModelAndView displayTeacherNewScheduleModal(@PathVariable("id") Long id,
                                                       @PathVariable("day") int dayOfWeek,
                                                       ModelMap model,
                                                       final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("eventTypes",
                    typeService.getAllEventTypesByUserRoles(user));
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            List<ScheduleEvent> actualEvents =
                    scheduleService.getNonCancelledEventsByOwnerAndDate(user, FIRST_MONDAY_OF_EPOCH.plusDays(dayOfWeek));
            Optional<ScheduleEvent> latestEvent = actualEvents.stream().reduce((first, second) -> second);
            LocalDate dateToStartNextEvent =
                    latestEvent
                            .map(event -> Optional.ofNullable(event.getEndOfEvent())
                                    .map(LocalDateTime::toLocalDate)
                                    .orElse(event.getStartOfEvent().toLocalDate()))
                            .orElse(FIRST_MONDAY_OF_EPOCH.plusDays(dayOfWeek));
            LocalTime timeToStartNextEvent =
                    latestEvent
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
                            .withEventType(latestEvent.map(ScheduleEvent::getDescription).toString())
                            .build());
            modelAndView = new ModelAndView("teacher/teacher_schedule :: newSchedule", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/{id}/new-schedule/{day}/add")
    public ModelAndView processTeacherScheduleModalFormAddEvent(@PathVariable("id") Long id,
                                                                @PathVariable("day") int dayOfWeek,
                                                                @ModelAttribute("newEvent") ScheduleEventDTO newEvent,
                                                                ModelMap model,
                                                                final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent()) {
            Optional<ScheduleEventType> type = typeService.loadEventTypes().stream()
                    .filter(eventType -> eventType.getName().equals(newEvent.getEventType()))
                    .findAny();
            Integer duration = type.map(ScheduleEventType::getDurationInMinutes)
                    .orElse(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            ScheduleEventDTO anEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                    .withDate(FIRST_MONDAY_OF_EPOCH.plusDays(dayOfWeek))
                    .withCreated(LocalDateTime.now())
                    .withIsOpen(true)
                    .withTitle(type.map(ScheduleEventType::getName).orElse(UK_EVENT_TYPE_NOT_DEFINED))
                    .withDescription(newEvent.getDescription())
                    .withEventType(newEvent.getEventType())
                    .withStartTime(newEvent.getStartTime())
                    .build();

            scheduleService.createEventWithDuration(
                    optionalUser.get(),
                    anEvent,
                    duration);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
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
                Optional.ofNullable(teacher).ifPresent(t ->
                        userService.createAndSaveFakeUserWithKeyAndRoleName(t.getVerificationKey(),
                                "ROLE_TEACHER"));
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
    @PostMapping("/{id}/delete")
    public ModelAndView processTeacherListFormDelete(@PathVariable("id") Long id, final Principal principal) {
        //TODO Add delete confirmation
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

        return redirectByRole(principal);
    }

    /*
          When key change confirmed:
          DTO Receives a NEW KEY which is instantly assigned, an old key is removed from user (if present),
          the old user is DELETED from database, and a NEW one created for the key recently assigned to DTO
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
                .orElse(null);
    }

    private String getEventDetails(ScheduleEvent event) {
        return event.getStartOfEvent()
                .format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                + " "
                + event.getTitle()
                + (event.getDescription().isEmpty() ? "" : ": ")
                + event.getDescription();
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

    private boolean isWorkingDay(int dayOfWeek) {
        return dayOfWeek >= 0 && dayOfWeek < 5;
    }
}
