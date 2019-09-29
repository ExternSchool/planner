package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.PersonDTO;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

public interface GuestService {
    ModelAndView prepareGuestList();

    ModelAndView processCreatePersonProfileModal(PersonDTO personDTO, BindingResult bindingResult, Principal principal);

    ModelAndView displayPersonProfile(Principal principal, Boolean isNew);

    ModelAndView displayPersonProfileToEdit(Long id);

    ModelAndView processFormPersonProfileActionSave(PersonDTO personDTO, BindingResult bindingResult, ModelMap model, Principal principal);

    ModelAndView redirectByRole(Principal principal);

    ModelAndView displayPersonListFormDeleteModal(Long id, ModelMap model);

    ModelAndView deletePersonProfile(Long id);

    ModelAndView displaySubscriptionsToGuest(ModelMap model, Principal principal);

    ModelAndView displaySubscriptionsToAdmin(Long guestId, ModelMap model);

    ModelAndView displayOfficialsListToGuest(ModelMap model, Principal principal);

    ModelAndView prepareScheduleModelAndView(Long guestId, Long officialId, ModelMap model);

    ModelAndView processSubscriptionModal(Long guestId, Long officialId, Long eventId, ModelMap model);

    ModelAndView displayUnsubscribeModal(Long guestId, Long officialId, Long eventId, ModelMap model);

    ModelAndView processUnsubscribeModal(Long guestId, Long officialId, Long eventId, ModelMap model);
}
