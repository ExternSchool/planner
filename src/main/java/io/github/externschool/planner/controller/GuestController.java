package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ParticipantDTO;
import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.Participant;
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
import io.github.externschool.planner.exceptions.UserCanNotHandleEventException;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
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

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_NOT_DEFINED;
import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_SUBSCRIBE_SCHEDULE_EVENT_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_WEEK_WORKING_DAYS;

@Controller
@RequestMapping("/guest")
public class GuestController {
    private final PersonService personService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final RoleService roleService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final ScheduleService scheduleService;
    private final ScheduleEventTypeService scheduleEventTypeService;

    @Autowired
    public GuestController(final PersonService personService,
                           final ConversionService conversionService,
                           final VerificationKeyService keyService,
                           final RoleService roleService,
                           final UserService userService,
                           final TeacherService teacherService,
                           final ScheduleService scheduleService,
                           final ScheduleEventTypeService scheduleEventTypeService) {
        this.personService = personService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
        this.teacherService = teacherService;
        this.scheduleService = scheduleService;
        this.scheduleEventTypeService = scheduleEventTypeService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/")
    @SuppressWarnings("unchecked")
    public ModelAndView displayGuestList(@RequestParam(value = "search", required = false) String request) {
        ModelAndView modelAndView = prepareGuestList();
        if (request != null) {
            modelAndView.addObject("guests",
                    Utils.searchRequestFilter((List<PersonDTO>)(modelAndView.getModel().get("guests")), request));
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search/{id}")
    public ModelAndView displayPersonWithSearch(@PathVariable(value = "id", required = false) Long id,
                                                 Principal principal) {
        Person person = personService.findPersonById(id);
        if (person != null) {
            return displayGuestList(person.getLastName());
        }

        return redirectByRole(principal);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/create")
    public ModelAndView showCreatePersonProfileModal() {
        ModelAndView modelAndView = prepareGuestList();
        modelAndView.setViewName("guest/guest_list :: createAccount");

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public ModelAndView processCreatePersonProfileModal(@ModelAttribute("person") @Valid PersonDTO personDTO,
                                                        BindingResult bindingResult,
                                                        ModelMap model,
                                                        Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
        } catch (BindingResultException e) {
            ModelAndView modelAndView = prepareGuestList();
            modelAndView.setViewName("guest/guest_list :: createAccount");
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("person", personDTO);

            return modelAndView;
        }

        personDTO.setVerificationKey(keyService.saveOrUpdateKey(Optional.ofNullable(personDTO.getVerificationKey())
                .orElse(new VerificationKey())));
        Person person = personService.saveOrUpdatePerson(conversionService.convert(personDTO, Person.class));
        if (isPrincipalAnAdmin(principal)) {
            updateUserWhenSaveUpdatePerson(person);
        }

        return new ModelAndView("redirect:/guest/" + person.getId() + "/official/schedule");
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/profile")
    public ModelAndView displayPersonProfile(final Principal principal,
                                             @RequestParam(value = "isNew", required = false) Boolean isNew) {
        Long id = userService.getUserByEmail(principal.getName()).getVerificationKey().getPerson().getId();
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false).addObject("isNew", isNew == null ? false : isNew);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayPersonProfileToEdit(@PathVariable("id") Long id){
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    /**
     * Updates current user with a new key and a new person when a valid key is provided in submitted form;
     * a user attached to the new key before, as well as current user's previous person and key are dropped.
     * If submitted key is the same as before, updates person data.
     *
     * @param personDTO
     * @param bindingResult
     * @param model
     * @param principal
     * @return
     */
    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormPersonProfileActionSave(@ModelAttribute("person") @Valid PersonDTO personDTO,
                                                           BindingResult bindingResult,
                                                           ModelMap model,
                                                           Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                Optional.ofNullable((bindingResult.getAllErrors().get(0)).getDefaultMessage())
                        .filter(message -> message.contains("verificationKey"))
                        .ifPresent(r -> {throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);});

                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
        } catch (BindingResultException | EmailExistsException | KeyNotValidException | RoleNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("guest/person_profile", model);
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("person", personDTO);
            modelAndView.addObject("isNew", false);

            return modelAndView;
        }
            // always exists since every user receives a key and a person when /init is processed at UserController.
            Person person = personService.findPersonById(personDTO.getId());
            VerificationKey key = person.getVerificationKey();
            VerificationKey newKey = personDTO.getVerificationKey();
            User user = key.getUser();
            if (key.equals(newKey)) {
                person = personService.saveOrUpdatePerson(conversionService.convert(personDTO, Person.class));
                personService.saveOrUpdatePerson(person);
            } else {
                // assigning received new key with a new person to the current user
                //remove all related events participants
                scheduleService.getParticipantsByUser(user).forEach(scheduleService::removeParticipant);
                //remove an old key from the current user
                user.removeVerificationKey();
                //delete an old person and a bound key
                personService.deletePerson(person);
                //delete an old user from the new key
                userService.deleteUser(newKey.getUser());
                user.addVerificationKey(newKey);
                userService.assignNewRolesByKey(user, newKey);
                userService.save(user);

                return new ModelAndView("redirect:/logout");
            }

        return redirectByRole(principal);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView processFormPersonProfileActionCancel(final Principal principal) {
        return redirectByRole(principal);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/delete-modal")
    public ModelAndView displayPersonListFormDeleteModal(final @PathVariable("id") Long id,
                                                         final ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("guest/guest_list :: deleteGuest", model);
        PersonDTO person = conversionService.convert(personService.findPersonById(id), PersonDTO.class);
        if (person != null) {
            modelAndView.addObject("person", person);
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView deletePersonProfile(@PathVariable("id") Long id){
        Person person = personService.findPersonById(id);
        VerificationKey key = person.getVerificationKey();
        Optional.ofNullable(person.getVerificationKey())
                .map(VerificationKey::getUser)
                .ifPresent(userService::deleteUser);
        personService.deletePerson(person);
        keyService.deleteById(key.getId());

        return new ModelAndView("redirect:/guest/");
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/subscriptions")
    public ModelAndView displaySubscriptionsToGuest(final ModelMap model, final Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        return prepareSubscriptionsModelAndView(user, model);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/subscriptions")
    public ModelAndView displaySubscriptionsToAdmin(@PathVariable("gid") Long guestId, final ModelMap model) {
        User user = Optional.ofNullable(personService.findPersonById(guestId))
                .map(Person::getVerificationKey)
                .map(VerificationKey::getUser)
                .orElse(null);

        return prepareSubscriptionsModelAndView(user, model);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/official/schedule")
    public ModelAndView displayOfficialsListToGuest(final ModelMap model, final Principal principal) {
        Long guestId = Optional.ofNullable(userService.getUserByEmail(principal.getName()))
                .map(User::getVerificationKey)
                .map(VerificationKey::getPerson)
                .map(Person::getId)
                .orElse(null);

        return prepareScheduleModelAndView(guestId, null, model);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/official/schedule")
    public ModelAndView displayOfficialsListToAdmin(@PathVariable("gid") Long guestId, final ModelMap model) {

        return prepareScheduleModelAndView(guestId, null, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/official/{id}/schedule")
    public ModelAndView displayOfficialSchedule(@PathVariable("gid") Long guestId,
                                               @PathVariable("id") Long officialId,
                                               final ModelMap model) {

        return prepareScheduleModelAndView(guestId, officialId, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/official/{id}/event/{event}/subscribe")
    public ModelAndView displaySubscriptionModal(@PathVariable("gid") Long guestId,
                                                   @PathVariable("id") Long officialId,
                                                   @PathVariable("event") Long eventId,
                                                   ModelMap model) {
        ModelAndView modelAndView = prepareScheduleModelAndView(guestId, officialId, model);
        modelAndView.addObject("event",
                conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("guest/guest_schedule :: subscribeEvent");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping("/{gid}/official/{id}/event/{event}/subscribe")
    public ModelAndView processSubscriptionModal(@PathVariable("gid") Long guestId,
                                                   @PathVariable("id") Long officialId,
                                                   @PathVariable("event") Long eventId,
                                                   ModelMap model) {
        ModelAndView modelAndView = new ModelAndView(
                "redirect:/guest/" + guestId + "/official/" + officialId + "/schedule", model);
        try {
            subscribeScheduleEvent(guestId, eventId);
        } catch (UserCanNotHandleEventException e) {
            modelAndView = prepareScheduleModelAndView(guestId, officialId, model);
            modelAndView.addObject("error", e.getMessage());
        }

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/official/{id}/event/{event}/unsubscribe")
    public ModelAndView displayUnsubscribeModal(@PathVariable("gid") Long guestId,
                                                      @PathVariable("id") Long officialId,
                                                      @PathVariable("event") Long eventId,
                                                      ModelMap model,
                                                      final Principal principal) {
        ModelAndView modelAndView = prepareScheduleModelAndView(guestId, officialId, model);
        modelAndView.addObject("event",
                conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("guest/guest_schedule :: unsubscribe");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping("/{gid}/official/{id}/event/{event}/unsubscribe")
    public ModelAndView processUnsubscribeModal(@PathVariable("gid") Long guestId,
                                                      @PathVariable("id") Long officialId,
                                                      @PathVariable("event") Long eventId,
                                                      ModelMap model) {
        ModelAndView modelAndView = new ModelAndView(
                "redirect:/guest/" + guestId + "/official/" + officialId + "/schedule", model);
        try {
            unsubscribeScheduleEvent(guestId, eventId);
        } catch (UserCanNotHandleEventException e) {
            modelAndView = prepareScheduleModelAndView(guestId, officialId, model);
            modelAndView.addObject("error", e.getMessage());
        }

        return modelAndView;
    }

    private void subscribeScheduleEvent(Long guestId, Long eventId) throws UserCanNotHandleEventException {
        User user = Optional.ofNullable(personService.findPersonById(guestId))
                .map(Person::getVerificationKey)
                .map(VerificationKey::getUser)
                .orElse(null);
        Optional<Participant> participant = scheduleService.addParticipant(user, scheduleService.getEventById(eventId));
        if (!participant.isPresent()) {
            throw new UserCanNotHandleEventException(UK_SUBSCRIBE_SCHEDULE_EVENT_ERROR_MESSAGE);
        }
    }

    private void unsubscribeScheduleEvent(Long guestId, Long eventId) throws UserCanNotHandleEventException {
        User user = Optional.ofNullable(personService.findPersonById(guestId))
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

    private ModelAndView prepareGuestList() {
        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        List<PersonDTO> guests = personService.findAllByOrderByName().stream()
                .map(p -> p.getClass().equals(Person.class) ? conversionService.convert(p, PersonDTO.class) : null)
                .filter(Objects::nonNull)
                .filter(p -> (p.getVerificationKey() == null)
                        || (p.getVerificationKey() != null && Optional.ofNullable(keyService.findKeyByValue(
                        p.getVerificationKey().getValue()).getUser())
                        .filter(user -> !user.getRoles().contains(roleAdmin)).isPresent()))
                .collect(Collectors.toList());
        ModelAndView modelAndView = new ModelAndView("guest/guest_list", "guests", guests);
        modelAndView.addObject("person", new PersonDTO());

        return modelAndView;
    }

    private ModelAndView prepareScheduleModelAndView(final Long guestId, final Long officialId, final ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("guest/guest_schedule", model);

        // TODO Add invalid guestId error checking
        LocalDate currentWeekFirstDay = scheduleService.getCurrentWeekFirstDay();
        LocalDate nextWeekFirstDay = scheduleService.getNextWeekFirstDay();
        List<LocalDate> currentWeekDates = scheduleService.getWeekStartingFirstDay(currentWeekFirstDay);
        List<LocalDate> nextWeekDates = scheduleService.getWeekStartingFirstDay(nextWeekFirstDay);
        List<List<ScheduleEvent>> currentWeekEvents = new ArrayList<>();
        List<List<ScheduleEvent>> nextWeekEvents = new ArrayList<>();
        TeacherDTO officialTeacher = new TeacherDTO();
        PersonDTO guestPerson = conversionService.convert(personService.findPersonById(guestId), PersonDTO.class);
        Optional<ScheduleEvent> subscribedEvent = Optional.empty();
        Optional<LocalDateTime> mostRecentUpdate = Optional.empty();
        long incomingEventsNumber = 0;

        Optional<User> optionalOfficialUser = getOptionalOfficialUser(officialId);
        Optional<User> optionalGuestUser = getOptionalGuestUser(guestId);
        if (officialId != null
                && optionalGuestUser.isPresent()
                && optionalOfficialUser.isPresent()) {
            User officialUser = optionalOfficialUser.get();
            User guestUser = optionalGuestUser.get();
            officialTeacher = conversionService.convert(teacherService.findTeacherById(officialId), TeacherDTO.class);

            // when user has any subscribed event no more events available to subscribe are shown
            List<ScheduleEvent> allCurrentEvents = scheduleService.getEventsByOwnerStartingBetweenDates(
                    officialUser,
                    LocalDate.now(),
                    currentWeekFirstDay.plusDays(14));
            subscribedEvent = allCurrentEvents.stream()
                    .filter(event -> !event.isCancelled())
                    .map(ScheduleEvent::getParticipants)
                    .flatMap(Set::stream)
                    .filter(participant -> participant.getUser().equals(guestUser))
                    .findFirst()
                    .map(Participant::getEvent);
            if (subscribedEvent.isPresent()) {
                ScheduleEvent singleEvent = subscribedEvent.get();
                addByDatesSingletonListToEventsListOfLists(currentWeekDates, currentWeekEvents, singleEvent);
                addByDatesSingletonListToEventsListOfLists(nextWeekDates, nextWeekEvents, singleEvent);
            } else {
                currentWeekDates.forEach(date -> currentWeekEvents.add(getOpenEventsAvailableToGuest(officialUser, date)));
                nextWeekDates.forEach(date -> nextWeekEvents.add(getOpenEventsAvailableToGuest(officialUser, date)));
            }
            List<ScheduleEvent> incomingEvents = filterEventsAvailableToGuest(
                    guestUser,
                    scheduleService.getEventsByOwnerStartingBetweenDates(
                            officialUser,
                            currentWeekFirstDay,
                            currentWeekFirstDay.plusDays(13)));
            mostRecentUpdate = incomingEvents.stream().map(ScheduleEvent::getModifiedAt).max(Comparator.naturalOrder());
            incomingEvents = incomingEvents.stream().filter(ScheduleEvent::isOpen).collect(Collectors.toList());
            incomingEventsNumber = incomingEvents.stream().filter(event -> !event.isCancelled()).count();
        } else {
            currentWeekDates.forEach(date -> currentWeekEvents.add(new ArrayList<>()));
            nextWeekDates.forEach(date -> nextWeekEvents.add(new ArrayList<>()));
        }

        modelAndView.addObject("guest", guestPerson);
        modelAndView.addObject("official", officialTeacher);
        modelAndView.addObject("officials", teacherService.findAllOfficials());
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
        ModelAndView modelAndView = new ModelAndView("guest/guest_subscriptions", model);
        modelAndView.addObject("guest", Optional.ofNullable(user)
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

    private List<ScheduleEvent> filterEventsAvailableToGuest(final User guest, final List<ScheduleEvent> events) {
        Role role = roleService.getRoleByName("ROLE_GUEST");
        List<ScheduleEventType> availableTypes = scheduleEventTypeService.loadEventTypes().stream()
                .filter(type -> type.getParticipants().contains(role))
                .collect(Collectors.toList());

        return events.stream()
                .filter(event -> availableTypes.contains(event.getType()))
                .filter(event -> event.getStartOfEvent().isAfter(LocalDateTime.now()
                        // min date and time before new appointments
                        .plus(DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT)
                        .plus(HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT)))
                .collect(Collectors.toList());
    }

    private List<ScheduleEvent> getOpenEventsAvailableToGuest(User user, LocalDate date) {

        return filterEventsAvailableToGuest(user, scheduleService.getNonCancelledEventsByOwnerAndDate(user, date))
                .stream()
                .filter(ScheduleEvent::isOpen)
                .collect(Collectors.toList());
    }

    private Optional<User> getOptionalOfficialUser(final Long id) {
        return Optional.ofNullable(teacherService.findTeacherById(id))
                .flatMap(teacher -> Optional.ofNullable(teacher.getVerificationKey()).map(VerificationKey::getUser));
    }

    private Optional<User> getOptionalGuestUser(final Long id) {
        return Optional.ofNullable(personService.findPersonById(id))
                .flatMap(guest -> Optional.ofNullable(guest.getVerificationKey()).map(VerificationKey::getUser));
    }

    private ModelAndView redirectByRole(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        if (user != null && user.getEmail() != null) {
            User userFound = userService.getUserByEmail(user.getEmail());
            if (userFound != null && userFound.getRoles().contains(roleService.getRoleByName("ROLE_ADMIN"))) {

                return new ModelAndView("redirect:/guest/");
            }
        }

        return new ModelAndView("redirect:/");
    }

    private ModelAndView showPersonProfileForm(PersonDTO personDTO, Boolean isNew){
        ModelAndView modelAndView = new ModelAndView("guest/person_profile");
        modelAndView.addObject("person", personDTO);
        modelAndView.addObject("isNew",isNew);

        return modelAndView;
    }

    //TODO probably Move to User Service
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

    private void updateUserWhenSaveUpdatePerson(Person person) {
        Optional<User> user = Optional.ofNullable(person)
                .map(Person::getVerificationKey)
                .map(VerificationKey::getUser)
                .map(u -> userService.save(userService.assignNewRolesByKey(u, u.getVerificationKey())));
        if (!user.isPresent()) {
            Optional.ofNullable(person).ifPresent(p ->
                    userService.createAndSaveFakeUserWithKeyAndRoleName(p.getVerificationKey(),
                            "ROLE_GUEST"));
        }
    }
}
