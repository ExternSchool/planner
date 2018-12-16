package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.KeyNotValidException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
import static io.github.externschool.planner.util.Constants.DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT;
import static io.github.externschool.planner.util.Constants.HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_NOT_DEFINED;
import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_WEEK_DAYS_FRIDAY;
import static io.github.externschool.planner.util.Constants.UK_WEEK_DAYS_MONDAY;
import static io.github.externschool.planner.util.Constants.UK_WEEK_DAYS_THIRSDAY;
import static io.github.externschool.planner.util.Constants.UK_WEEK_DAYS_TUESAY;
import static io.github.externschool.planner.util.Constants.UK_WEEK_DAYS_WEDNESDAY;

@Controller
@Transactional
@RequestMapping("/guest")
public class GuestController {
    private final PersonService personService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final RoleService roleService;
    private final UserService userService;
    @Autowired private TeacherService teacherService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private ScheduleEventTypeService scheduleEventTypeService;

    public GuestController(final PersonService personService,
                           final ConversionService conversionService,
                           final VerificationKeyService keyService,
                           final RoleService roleService,
                           final UserService userService) {
        this.personService = personService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/")
    public ModelAndView showGuestList(){
        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        List<PersonDTO> persons = personService.findAllByOrderByName().stream()
                .map(p -> p.getClass().equals(Person.class) ? conversionService.convert(p, PersonDTO.class) : null)
                .filter(Objects::nonNull)
                .filter(p -> (p.getVerificationKey() == null)
                        || (p.getVerificationKey() != null
                            && keyService.findKeyByValue(p.getVerificationKey().getValue()).getUser() != null
                            && !keyService.findKeyByValue(p.getVerificationKey().getValue()).getUser().getRoles()
                        .contains(roleAdmin)))
                .collect(Collectors.toList());

        return new ModelAndView("guest/person_list", "persons", persons);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/profile")
    public ModelAndView showFormPersonProfile(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();
        PersonDTO personDTO =  conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView showFormPersonProfileToEdit(@PathVariable("id") Long id){
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView deletePersonProfile(@PathVariable("id") Long id){
        personService.deletePerson(personService.findPersonById(id));

        return new ModelAndView("redirect:/guest/");
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormPersonProfileActionSave(@ModelAttribute("person") @Valid PersonDTO personDTO,
                                                           BindingResult bindingResult,
                                                           Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                Optional.ofNullable((bindingResult.getAllErrors().get(0)).getDefaultMessage())
                        .filter(message -> message.contains("verificationKey"))
                        .ifPresent(r -> {throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);});

                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }

            Person persistedPerson = personService.findPersonById(personDTO.getId());
            VerificationKey persistedKey = persistedPerson.getVerificationKey();
            User user = persistedKey.getUser();
            VerificationKey newKey = personDTO.getVerificationKey();
            if (newKey != null && newKey != persistedKey) {
                if (newKey.getUser() != null) {
                    throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);
                }
                Person newPerson = newKey.getPerson();
                if (user != null && newPerson != null && newPerson.getClass() != Person.class) {
                    user.removeVerificationKey();
                    personService.deletePerson(persistedPerson);
                    user.addVerificationKey(newKey);
                    userService.assignNewRolesByKey(user, newKey);
                    userService.save(user);
                    if (userService.findUserByEmail(principal.getName())
                            .getRoles()
                            .contains(roleService.getRoleByName("ROLE_ADMIN"))) {
                        return new ModelAndView("redirect:/guest/");
                    }

                    return new ModelAndView("redirect:/logout");
                }
            }
            personDTO.setVerificationKey(persistedKey);
            personService.saveOrUpdatePerson(conversionService.convert(personDTO, Person.class));
        } catch (BindingResultException | EmailExistsException | KeyNotValidException | RoleNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("guest/person_profile");
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("person", personDTO);
            modelAndView.addObject("isNew", false);

            return modelAndView;
        }

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView processFormPersonProfileActionCancel(final Principal principal) {

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/officer/schedule/")
    public ModelAndView displayOfficersListToGuest(final ModelMap model, final Principal principal) {
        Long guestId = Optional.ofNullable(userService.findUserByEmail(principal.getName()))
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .map(Person::getId)
                .orElse(null);

        return prepareModelAndView(guestId, null, model);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/officer/schedule/")
    public ModelAndView displayOfficersListToAdmin(@PathVariable("gid") Long guestId, final ModelMap model) {

        return prepareModelAndView(guestId, null, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/officer/{id}/schedule")
    public ModelAndView displayOfficerSchedule(@PathVariable("gid") Long guestId,
                                               @PathVariable("id") Long officerId,
                                               final ModelMap model) {

        return prepareModelAndView(guestId, officerId, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/officer/{id}/event/{event}/reserve")
    public ModelAndView displayNewReservationModal(@PathVariable("gid") Long guestId,
                                                   @PathVariable("id") Long officerId,
                                                   @PathVariable("event") int eventId,
                                                   ModelMap model) {
        ModelAndView modelAndView = prepareModelAndView(guestId, officerId, model);
        modelAndView.addObject("event",
                conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("guest/guest_schedule :: reserveEvent");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/officer/{id}/event/{event}/add")
    public ModelAndView processNewReservationModal(@PathVariable("gid") Long guestId,
                                                   @PathVariable("id") Long officerId,
                                                   @PathVariable("event") int eventId,
                                                   ModelMap model,
                                                   final Principal principal) {
        // TODO REPLACE with implementation
        System.out.println("Creating Reservation");

        return redirectEventByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/officer/{id}/event/{event}/cancel")
    public ModelAndView displayCancelReservationModal(@PathVariable("gid") Long guestId,
                                                      @PathVariable("id") Long officerId,
                                                      @PathVariable("event") int eventId,
                                                      ModelMap model,
                                                      final Principal principal) {
        ModelAndView modelAndView = prepareModelAndView(guestId, officerId, model);
        modelAndView.addObject("event",
                conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("guest/guest_schedule :: cancelReservation");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/officer/{id}/event/{event}/delete")
    public ModelAndView processCancelReservationModal(@PathVariable("gid") Long guestId,
                                                      @PathVariable("id") Long officerId,
                                                      @PathVariable("event") int eventId,
                                                      ModelMap model,
                                                      final Principal principal) {
        // TODO REPLACE with implementation
        System.out.println("Cancelling Reservation");


        return redirectEventByRole(userService.findUserByEmail(principal.getName()));
    }

    private ModelAndView prepareModelAndView(final Long guestId, final Long officerId, final ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("guest/guest_schedule", model);

        // TODO Add invalid guestId error checking
        List<LocalDate> currentWeek = scheduleService.getWeekStartingFirstDay(scheduleService.getCurrentWeekFirstDay());
        List<LocalDate> nextWeek = scheduleService.getWeekStartingFirstDay(scheduleService.getNextWeekFirstDay());
        List<List<ScheduleEventDTO>> currentWeekEvents = new ArrayList<>();
        List<List<ScheduleEventDTO>> nextWeekEvents = new ArrayList<>();
        TeacherDTO officerTeacher = new TeacherDTO();
        PersonDTO guestPerson = conversionService.convert(personService.findPersonById(guestId), PersonDTO.class);
        Optional<ScheduleEventDTO> reservedEvent = Optional.empty();

        Optional<User> optionalOfficerUser = getOptionalOfficerUser(officerId);
        Optional<User> optionalGuestUser = getOptionalGuestUser(guestId);
        if (officerId != null
                && optionalGuestUser.isPresent()
                && optionalOfficerUser.isPresent()) {
            User officerUser = optionalOfficerUser.get();
            User guestUser = optionalGuestUser.get();
            officerTeacher = conversionService.convert(teacherService.findTeacherById(officerId), TeacherDTO.class);

            currentWeek.forEach(date -> currentWeekEvents.add(getEventsAvailableToGuest(officerUser, date)));
            nextWeek.forEach(date -> nextWeekEvents.add(getEventsAvailableToGuest(officerUser, date)));
            List<ScheduleEvent> events = scheduleService.getEventsByOwner(officerUser);
            reservedEvent = events.stream()
                    .filter(event -> scheduleService.getParticipantByUserAndEvent(guestUser, event).isPresent())
                    .map(e -> conversionService.convert(e, ScheduleEventDTO.class))
                    .findFirst();
        } else {
            currentWeek.forEach(date -> currentWeekEvents.add(new ArrayList<>()));
            nextWeek.forEach(date -> nextWeekEvents.add(new ArrayList<>()));
        }

        modelAndView.addObject("guest", guestPerson);
        modelAndView.addObject("officer", officerTeacher);
        modelAndView.addObject("officers", teacherService.findAllOfficers());
        modelAndView.addObject("weekDays", Arrays.asList(
                UK_WEEK_DAYS_MONDAY,
                UK_WEEK_DAYS_TUESAY,
                UK_WEEK_DAYS_WEDNESDAY,
                UK_WEEK_DAYS_THIRSDAY,
                UK_WEEK_DAYS_FRIDAY));
        modelAndView.addObject("currentWeek", currentWeek);
        modelAndView.addObject("nextWeek", nextWeek);
        modelAndView.addObject("currentWeekEvents", currentWeekEvents);
        modelAndView.addObject("nextWeekEvents", nextWeekEvents);
        // min date and time before new appointments
        modelAndView.addObject("latestDate", LocalDate.now().plus(DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT));
        modelAndView.addObject("latestTime", LocalTime.now().plus(HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT));
        modelAndView.addObject("event",
                reservedEvent
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

    private List<ScheduleEventDTO> getEventsAvailableToGuest(User user, LocalDate date) {
        Role role = roleService.getRoleByName("ROLE_GUEST");
        List<ScheduleEventType> availableTypes = scheduleEventTypeService.loadEventTypes().stream()
                .filter(type -> type.getParticipants().contains(role))
                .collect(Collectors.toList());

        return convertEventsToDTO(
                scheduleService.getActualEventsByOwnerAndDate(user, date).stream()
                        .filter(event -> availableTypes.contains(event.getType()))
                        .collect(Collectors.toList()));
    }

    private Optional<User> getOptionalOfficerUser(final Long id) {
        return Optional.ofNullable(teacherService.findTeacherById(id))
                .map(teacher -> Optional.ofNullable(teacher.getVerificationKey()).map(VerificationKey::getUser))
                .orElse(null);
    }

    private Optional<User> getOptionalGuestUser(final Long id) {
        return Optional.ofNullable(personService.findPersonById(id))
                .map(guest -> Optional.ofNullable(guest.getVerificationKey()).map(VerificationKey::getUser))
                .orElse(null);
    }

    private List<ScheduleEventDTO> convertEventsToDTO(List<ScheduleEvent> events) {
        return events.stream()
                .map(event -> conversionService.convert(event, ScheduleEventDTO.class))
                .collect(Collectors.toList());
    }

    private ModelAndView redirectEventByRole(User user) {
        if (userService.findUserByEmail(user.getEmail())
                .getRoles()
                .contains(roleService.getRoleByName("ROLE_ADMIN"))) {

            // TODO check this path
            return new ModelAndView("redirect:/guest/");
        }

        return new ModelAndView("redirect:/guest/officer/schedule/");
    }

    private ModelAndView redirectByRole(User user) {
        if (userService.findUserByEmail(user.getEmail())
                .getRoles()
                .contains(roleService.getRoleByName("ROLE_ADMIN"))) {

            return new ModelAndView("redirect:/guest/");
        }

        return new ModelAndView("redirect:/");
    }

    private ModelAndView showPersonProfileForm(PersonDTO personDTO, Boolean isNew){
        ModelAndView modelAndView = new ModelAndView("guest/person_profile");
        modelAndView.addObject("person", personDTO);
        modelAndView.addObject("isNew",isNew);

        return modelAndView;
    }
}
