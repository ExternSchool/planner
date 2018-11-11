package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
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
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
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
    public ModelAndView displayTeacherListForm() {
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
    public ModelAndView displayTeacherProfileForTeacher(final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Long id = Optional.ofNullable(userService.findUserByEmail(principal.getName())
                .getVerificationKey().getPerson().getId())
                .orElse(0L);

        Teacher teacher = teacherService.findTeacherById(id);
        if (teacher != null) {
            modelAndView = displayTeacherProfile(conversionService.convert(teacher, TeacherDTO.class));
        }

        return modelAndView;
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/visitors")
    public ModelAndView displayTeacherVisitorsToTeacher(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
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
            List<PersonDTO> visitors = new ArrayList<>();

            LocalDate start = LocalDate.now();
            LocalDate end = scheduleService.getNextWeekFirstDay().plusDays(6);
            List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(start, end))
                    .collect(Collectors.toList());
            for (LocalDate date : dates) {
                scheduleService.getActualEventsByOwnerAndDate(user, date).forEach(event -> {
                    event.getParticipants().stream()
                            .map(Participant::getUser)
                            .map(User::getVerificationKey)
                            .map(VerificationKey::getPerson)
                            .forEach(person -> {
                                if (person.getClass() == Student.class) {
                                    Optional.ofNullable(conversionService.convert(person, StudentDTO.class))
                                            .ifPresent(studentDTO -> {
                                                studentDTO.setOptionalData(
                                                        event.getStartOfEvent()
                                                                .format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                                                                + " "
                                                                + event.getDescription());
                                                students.add(studentDTO);
                                            });
                                } else {
                                    Optional.ofNullable(conversionService.convert(person, PersonDTO.class))
                                            .ifPresent(guestDTO -> {
                                                guestDTO.setOptionalData(
                                                        event.getStartOfEvent()
                                                                .format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                                                                + " "
                                                                + event.getDescription());
                                                visitors.add(guestDTO);
                                            });
                                }
                            });
                });
            }

            modelAndView = new ModelAndView("teacher/teacher_visitors", "teacher", teacherDTO);
            modelAndView.addObject("students", students);
            modelAndView.addObject("visitors", visitors);
        }

        return modelAndView;
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/schedule")
    public ModelAndView displayTeacherScheduleToTeacher(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
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

            List<LocalDate> currentWeek = scheduleService
                    .getWeekStartingFirstDay(scheduleService.getCurrentWeekFirstDay());
            List<LocalDate> nextWeek = scheduleService.getWeekStartingFirstDay(scheduleService.getNextWeekFirstDay());
            // standard week schedule has no real date to start, so FIRST_MONDAY_OF_EPOCH is used
            List<LocalDate> standardWeek = scheduleService.getWeekStartingFirstDay(FIRST_MONDAY_OF_EPOCH);
            List<List<ScheduleEventDTO>> currentWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> nextWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> standardWeekEvents = new ArrayList<>();

            currentWeek.forEach(date ->
                    currentWeekEvents.add(convertToDTO(scheduleService.getActualEventsByOwnerAndDate(user, date))));
            nextWeek.forEach(date ->
                    nextWeekEvents.add(convertToDTO(scheduleService.getActualEventsByOwnerAndDate(user, date))));
            standardWeek.forEach(date ->
                    standardWeekEvents.add(convertToDTO(scheduleService.getActualEventsByOwnerAndDate(user, date))));

            modelAndView = new ModelAndView("teacher/teacher_schedule", "teacher", teacherDTO);
            modelAndView.addObject("currentWeek", currentWeek);
            modelAndView.addObject("nextWeek", nextWeek);
            modelAndView.addObject("currentWeekEvents", currentWeekEvents);
            modelAndView.addObject("nextWeekEvents", nextWeekEvents);
            modelAndView.addObject("standardWeekEvents", standardWeekEvents);
            modelAndView.addObject("newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO().build());
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/event/{eid}/delete")
    public ModelAndView processTeacherEventDelete(@PathVariable("id") Long id,
                                                  @PathVariable("eid") Long eventId,
                                                  ModelMap model,
                                                  final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        ScheduleEvent event = scheduleService.getEventById(eventId);
        if (optionalUser != null && optionalUser.isPresent() && event != null) {
            scheduleService.deleteEvent(eventId);
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
            model.addAttribute("eventTypes", typeService.loadEventTypes());
            model.addAttribute(
                    "newEvent",
                    ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO().build());
            model.addAttribute(
                    "teacher",
                    conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class));
            model.addAttribute("thisDay", dayOfWeek);
            model.addAttribute(
                    "thisDayEvents",
                    convertToDTO(scheduleService
                            .getActualEventsByOwnerAndDate(user, FIRST_MONDAY_OF_EPOCH.plusDays(dayOfWeek))));
            modelAndView = new ModelAndView("teacher/teacher_schedule :: newSchedule", model);
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
            modelAndView = new ModelAndView("teacher/teacher_schedule :: deleteCurrentWeekDay", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/current-week/{day}/delete")
    public ModelAndView processTeacherDeleteCurrentWeekDay(@PathVariable("id") Long id,
                                                           @PathVariable("day") int dayOfWeek,
                                                           ModelMap model,
                                                           final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent() && isWorkingDay(dayOfWeek)) {
            List<ScheduleEvent> events = scheduleService.getActualEventsByOwnerAndDate(
                    optionalUser.get(),
                    scheduleService.getCurrentWeekFirstDay().plus(Period.ofDays(dayOfWeek)));
            Executor executor = Executors.newSingleThreadExecutor();
            events.forEach(event -> {
                executor.execute(() -> emailService.sendCancelEventMail(event));
                scheduleService.cancelEvent(event.getId());
            });

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
            modelAndView = new ModelAndView("teacher/teacher_schedule :: deleteNextWeekDay", model);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/next-week/{day}/delete")
    public ModelAndView processTeacherDeleteNextWeekDay(@PathVariable("id") Long id,
                                                        @PathVariable("day") int dayOfWeek,
                                                        ModelMap model,
                                                        final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Optional<User> optionalUser = getOptionalUser(id);
        if (optionalUser != null && optionalUser.isPresent() && isWorkingDay(dayOfWeek)) {
            List<ScheduleEvent> events = scheduleService.getActualEventsByOwnerAndDate(
                    optionalUser.get(),
                    scheduleService.getNextWeekFirstDay().plus(Period.ofDays(dayOfWeek)));
            Executor executor = Executors.newSingleThreadExecutor();
            events.forEach(event -> {
                executor.execute(() -> emailService.sendCancelEventMail(event));
                scheduleService.cancelEvent(event.getId());
            });

            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
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
            ScheduleEventDTO anEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                    .withDate(FIRST_MONDAY_OF_EPOCH.plusDays(dayOfWeek))
                    .withCreated(LocalDateTime.now())
                    .withIsOpen(true)
                    .withTitle(teacherService.findTeacherById(id).getShortName())
                    .withDescription(newEvent.getEventType())
                    .withEventType(newEvent.getEventType())
                    .withStartTime(newEvent.getStartTime())
                    .build();

            // TODO Add duration in dependence to the schedule event type
            scheduleService.createEventWithDuration(optionalUser.get(), anEvent, 45);
            modelAndView = new ModelAndView("redirect:/teacher/" + id + "/schedule");
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayTeacherProfileToEdit(@PathVariable("id") Long id, final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Teacher teacher = teacherService.findTeacherById(Optional.ofNullable(id).orElse(0L));
        if(teacher != null) {
            TeacherDTO teacherDTO = conversionService.convert(teacher, TeacherDTO.class);
            modelAndView = displayTeacherProfile(teacherDTO);
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayTeacherProfileToAdd() {
        return displayTeacherProfile(new TeacherDTO());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView processTeacherListFormDelete(@PathVariable("id") Long id, final Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        Teacher teacher = teacherService.findTeacherById(id);
        if(teacher != null) {
            //TODO Add deletion confirmation
            teacherService.deleteTeacherById(id);
        }

        return modelAndView;
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processTeacherProfileFormSave(@ModelAttribute("teacher") TeacherDTO teacherDTO,
                                                      final Principal principal) {
        if (isAdmin(principal)
                && (teacherDTO.getId() == null || teacherService.findTeacherById(teacherDTO.getId()) == null)) {
            if (teacherDTO.getVerificationKey() == null) {
                teacherDTO.setVerificationKey(new VerificationKey());
            }
            keyService.saveOrUpdateKey(teacherDTO.getVerificationKey());
        }
        Teacher teacher = conversionService.convert(teacherDTO, Teacher.class);
        teacherService.saveOrUpdateTeacher(teacher);

        if (isAdmin(principal)) {
            Optional.ofNullable(teacher)
                    .map(Teacher::getVerificationKey)
                    .map(VerificationKey::getUser)
                    .ifPresent(user -> userService.saveOrUpdate(
                            userService.assignNewRolesByKey(user, user.getVerificationKey())));
        }

        return redirectByRole(principal);
    }

    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping(value = "/update")
    public ModelAndView processTeacherProfileFormCancel(final Principal principal) {
        return redirectByRole(principal);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/{id}/new-key")
    public ModelAndView processTeacherProfileFormActionNewKey(@PathVariable("id") Long id, Principal principal) {
        ModelAndView modelAndView = redirectByRole(principal);
        if (id == null) {
            return modelAndView;
        }
        /*
          When key change confirmed:
          DTO Receives a NEW KEY which is instantly assigned, an old key is removed from user (if present),
          user receives Guest role
         */
        TeacherDTO teacherDTO = Optional.ofNullable(teacherService.findTeacherById(id))
                .map(teacher -> conversionService.convert(teacher, TeacherDTO.class))
                .map(t -> (TeacherDTO)keyService.setNewKeyToDTO(t))
                .orElse(new TeacherDTO());

        Optional.ofNullable(userService.findUserByEmail(teacherDTO.getEmail()))
                .ifPresent(user -> {
                    userService.createAndAddNewKeyAndPerson(user);
                    userService.saveOrUpdate(user);
                });

        modelAndView = displayTeacherProfile(teacherDTO);
        modelAndView.addObject("isNew", true);

        return modelAndView;
    }

    private ModelAndView displayTeacherProfile(TeacherDTO teacherDTO) {
        ModelAndView modelAndView = new ModelAndView("teacher/teacher_profile",
                "teacher", teacherDTO);
        modelAndView.addObject("isNew", isNew(teacherDTO));
        modelAndView.addObject("allSubjects", subjectService.findAllByOrderByTitle());

        return modelAndView;
    }

    private Boolean isNew(TeacherDTO teacherDTO) {
        return !Optional.ofNullable(teacherDTO)
                .map(t -> Optional.ofNullable(teacherService.findTeacherById(t.getId())).isPresent())
                .orElse(false);
    }

    private Boolean isAdmin(Principal principal) {
        return Optional.ofNullable(principal)
                .map(p -> userService.findUserByEmail(p.getName()))
                .map(User::getRoles)
                .map(roles -> roles.contains(roleService.getRoleByName("ROLE_ADMIN")))
                .orElse(Boolean.FALSE);
    }

    private ModelAndView redirectByRole(Principal principal) {
        if (isAdmin(principal)) {
            return new ModelAndView("redirect:/teacher/");
        }

        return new ModelAndView("redirect:/");
    }

    private Optional<User> getOptionalUser(final Long id) {
        return Optional.ofNullable(teacherService.findTeacherById(id))
                .map(teacher -> Optional.ofNullable(teacher.getVerificationKey()).map(VerificationKey::getUser))
                .orElse(null);
    }

    private List<ScheduleEventDTO> convertToDTO(List<ScheduleEvent> events) {
        return events.stream()
                .map(event -> conversionService.convert(event, ScheduleEventDTO.class))
                .collect(Collectors.toList());
    }

    private boolean isWorkingDay(int dayOfWeek) {
        return dayOfWeek >= 0 && dayOfWeek < 5;
    }
}
