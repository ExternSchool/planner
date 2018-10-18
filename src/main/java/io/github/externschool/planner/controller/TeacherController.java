package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.TeacherDTO;
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
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final ScheduleService scheduleService;
    private final ScheduleEventTypeService typeService;

    @Autowired
    public TeacherController(final TeacherService teacherService,
                             final SchoolSubjectService subjectService,
                             final ConversionService conversionService,
                             final VerificationKeyService keyService,
                             final UserService userService,
                             final RoleService roleService,
                             final ScheduleService scheduleService,
                             final ScheduleEventTypeService typeService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
        this.scheduleService = scheduleService;
        this.typeService = typeService;
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
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

        return displayTeacherProfile(teacherDTO);
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
        ModelAndView modelAndView = redirectByRole(userService.findUserByEmail(principal.getName()));
        Optional<User> optionalUser = Optional.ofNullable(teacherService.findTeacherById(id))
                .map(teacher -> Optional.ofNullable(teacher.getVerificationKey()).map(VerificationKey::getUser))
                .orElse(null);

        if (optionalUser != null && optionalUser.isPresent()) {
            TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

            List<LocalDate> currentWeek = scheduleService.getWeekStartingFirstDay(scheduleService.getCurrentWeekFirstDay());
            List<LocalDate> nextWeek = scheduleService.getWeekStartingFirstDay(scheduleService.getNextWeekFirstDay());
            // standard week schedule has no real date to start, so MIN value is used
            List<LocalDate> standardWeek = scheduleService.getWeekStartingFirstDay(LocalDate.MIN);
            List<List<ScheduleEventDTO>> currentWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> nextWeekEvents = new ArrayList<>();
            List<List<ScheduleEventDTO>> standardWeekEvents = new ArrayList<>();

            User user = optionalUser.get();
            currentWeek.forEach(date ->
                    currentWeekEvents.add(convertToDTO(scheduleService.getEventsByOwnerAndDate(user, date))));
            nextWeek.forEach(date ->
                    nextWeekEvents.add(convertToDTO(scheduleService.getEventsByOwnerAndDate(user, date))));
            standardWeek.forEach(date ->
                    standardWeekEvents.add(convertToDTO(scheduleService.getEventsByOwnerAndDate(user, date))));

            modelAndView = new ModelAndView("teacher/teacher_schedule", "teacher", teacherDTO);
            modelAndView.addObject("currentWeek", currentWeek);
            modelAndView.addObject("nextWeek", nextWeek);
            modelAndView.addObject("currentWeekEvents", currentWeekEvents);
            modelAndView.addObject("nextWeekEvents", nextWeekEvents);
            modelAndView.addObject("standardWeekEvents", standardWeekEvents);

        }

        return modelAndView;
    }

    // TODO Complete and add a test
    @Secured({"ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}/new-schedule/{day}")
    public ModelAndView displayTeacherNewScheduleModal(@PathVariable("id") Long id,
                                               @PathVariable("day") Long dayOfWeek,
                                               ModelMap model,
                                               final Principal principal) {

        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);
        List<ScheduleEventType> types = typeService.loadEventTypes();
        model.addAttribute("eventTypes", types);
        ScheduleEventDTO newEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(LocalDate.MIN.plusDays(dayOfWeek))
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .withTitle(teacherService.findTeacherById(id).getShortName())
                .build();
        model.addAttribute("newEvent", newEvent);
        model.addAttribute("teacher", teacherDTO);

        return new ModelAndView("teacher/teacher_schedule :: newSchedule", model);
    }

    // TODO replace this method with ScheduleToScheduleDTO converter
    // public access in test purpose only
    public List<ScheduleEventDTO> convertToDTO(List<ScheduleEvent> events) {
        return events.stream()
                .map(e -> new ScheduleEventDTO(
                        e.getId(),
                        LocalDate.from(e.getStartOfEvent()),
                        LocalTime.from(e.getStartOfEvent()),
                        // as a description add a list of participants with their grades, if they are students
                        // or add a name for the type of this event
                        e.getParticipants().isEmpty() ? e.getType().getName() : String.valueOf(
                                e.getParticipants().stream()
                                        .map(user -> Optional.ofNullable(user.getVerificationKey())
                                                .map(VerificationKey::getPerson)
                                                .map(person ->
                                                        person.getLastName() + " " + person.getFirstName() +
                                                                Optional.of((Student)person)
                                                                        .map(p -> ", " + String.valueOf(
                                                                                p.getGradeLevel().getValue()))
                                                                        .orElse(""))
                                                .orElse(""))
                                        .collect(Collectors.toList())),
                        e.isOpen(),
                        e.getType().getName(),
                        e.getTitle(),
                        e.getCreatedAt()))
                .collect(Collectors.toList());
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
    public ModelAndView displayTeacherProfileToEdit(@PathVariable("id") Long id) {
        Teacher teacher = teacherService.findTeacherById(id);
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findTeacherById(id), TeacherDTO.class);

        return displayTeacherProfile(teacherDTO);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayTeacherProfileToAdd() {
        return displayTeacherProfile(new TeacherDTO());
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
                .map(teacher -> conversionService.convert(teacher, TeacherDTO.class))
                .map(t -> (TeacherDTO)keyService.setNewKeyToDTO(t))
                .orElse(new TeacherDTO());

        Optional.ofNullable(userService.findUserByEmail(teacherDTO.getEmail()))
                .ifPresent(user -> {
                    userService.createAndAddNewKeyAndPerson(user);
                    userService.saveOrUpdate(user);
                });

        ModelAndView modelAndView = displayTeacherProfile(teacherDTO);
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
