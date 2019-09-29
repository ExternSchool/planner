package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.service.GuestService;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.ScheduleService;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/guest")
public class GuestController {
    private final PersonService personService;
    private final ConversionService conversionService;
    private final ScheduleService scheduleService;
    private final GuestService guestService;

    @Autowired
    public GuestController(final PersonService personService,
                           final ConversionService conversionService,
                           final ScheduleService scheduleService,
                           final GuestService guestService) {
        this.personService = personService;
        this.conversionService = conversionService;
        this.scheduleService = scheduleService;
        this.guestService = guestService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/")
    @SuppressWarnings("unchecked")
    public ModelAndView displayGuestList(@RequestParam(value = "search", required = false) final String request) {
        final ModelAndView modelAndView = guestService.prepareGuestList();
        if (request != null) {
            modelAndView.addObject("guests", Utils.searchRequestFilter((List<PersonDTO>)(modelAndView.getModel().get("guests")), request));
        }

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search/{id}")
    public ModelAndView displayPersonWithSearch(@PathVariable(value = "id", required = false) final Long id, final Principal principal) {
        return  Optional.ofNullable(personService.findPersonById(id)).map(person -> displayGuestList(person.getLastName()))
                .orElse(guestService.redirectByRole(principal));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/create")
    public ModelAndView showCreatePersonProfileModal() {
        final ModelAndView modelAndView = guestService.prepareGuestList();
        modelAndView.setViewName("guest/guest_list :: createAccount");

        return modelAndView;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public ModelAndView processCreatePersonProfileModal(@ModelAttribute("person") @Valid final PersonDTO personDTO,
                                                        final BindingResult bindingResult,
                                                        final Principal principal) {
        return guestService.processCreatePersonProfileModal(personDTO, bindingResult, principal);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/profile")
    public ModelAndView displayPersonProfile(final Principal principal, @RequestParam(value = "isNew", required = false) final Boolean isNew) {
        return guestService.displayPersonProfile(principal, isNew);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayPersonProfileToEdit(@PathVariable("id") Long id){
        return guestService.displayPersonProfileToEdit(id);
    }

    /**
     * Updates current user with a new key and a new person when a valid key is provided in submitted form;
     * a user attached to the new key before, as well as current user's previous person and key are dropped.
     * If submitted key is the same as before, updates person data.
     *
     * @param personDTO
     * @param bindingResult
     * @param principal
     * @return
     */
    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormPersonProfileActionSave(@ModelAttribute("person") @Valid final PersonDTO personDTO,
                                                           final BindingResult bindingResult,
                                                           final ModelMap model,
                                                           final Principal principal) {
        return guestService.processFormPersonProfileActionSave(personDTO, bindingResult, model, principal);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView processFormPersonProfileActionCancel(final Principal principal) {
        return guestService.redirectByRole(principal);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/delete-modal")
    public ModelAndView displayPersonListFormDeleteModal(final @PathVariable("id") Long id, final ModelMap model) {
        return guestService.displayPersonListFormDeleteModal(id, model);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView deletePersonProfile(@PathVariable("id") Long id) {
        return guestService.deletePersonProfile(id);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/subscriptions")
    public ModelAndView displaySubscriptionsToGuest(final ModelMap model, final Principal principal) {
        return guestService.displaySubscriptionsToGuest(model, principal);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/subscriptions")
    public ModelAndView displaySubscriptionsToAdmin(@PathVariable("gid") Long guestId, final ModelMap model) {
        return guestService.displaySubscriptionsToAdmin(guestId, model);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/official/schedule")
    public ModelAndView displayOfficialsListToGuest(final ModelMap model, final Principal principal) {
        return guestService.displayOfficialsListToGuest(model, principal);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{gid}/official/schedule")
    public ModelAndView displayOfficialsListToAdmin(@PathVariable("gid") Long guestId, final ModelMap model) {
        return guestService.prepareScheduleModelAndView(guestId, null, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/official/{id}/schedule")
    public ModelAndView displayOfficialSchedule(@PathVariable("gid") Long guestId, @PathVariable("id") Long officialId, final ModelMap model) {
        return guestService.prepareScheduleModelAndView(guestId, officialId, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/official/{id}/event/{event}/subscribe")
    public ModelAndView displaySubscriptionModal(@PathVariable("gid") Long guestId, 
                                                 @PathVariable("id") Long officialId,
                                                 @PathVariable("event") Long eventId,
                                                 ModelMap model) {
        ModelAndView modelAndView = guestService.prepareScheduleModelAndView(guestId, officialId, model);
        modelAndView.addObject("event", conversionService.convert(scheduleService.getEventById(eventId), ScheduleEventDTO.class));
        modelAndView.setViewName("guest/guest_schedule :: subscribeEvent");

        return modelAndView;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping("/{gid}/official/{id}/event/{event}/subscribe")
    public ModelAndView processSubscriptionModal(@PathVariable("gid") Long guestId, 
                                                 @PathVariable("id") Long officialId, 
                                                 @PathVariable("event") Long eventId,
                                                 ModelMap model) {
        return guestService.processSubscriptionModal(guestId, officialId, eventId, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @GetMapping("/{gid}/official/{id}/event/{event}/unsubscribe")
    public ModelAndView displayUnsubscribeModal(@PathVariable("gid") Long guestId,
                                                      @PathVariable("id") Long officialId,
                                                      @PathVariable("event") Long eventId,
                                                      ModelMap model) {
        return guestService.displayUnsubscribeModal(guestId, officialId, eventId, model);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping("/{gid}/official/{id}/event/{event}/unsubscribe")
    public ModelAndView processUnsubscribeModal(@PathVariable("gid") Long guestId,
                                                      @PathVariable("id") Long officialId,
                                                      @PathVariable("event") Long eventId,
                                                      ModelMap model) {
        return guestService.processUnsubscribeModal(guestId, officialId, eventId, model);
    }
}
