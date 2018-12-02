package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventTypeDTO;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE;

@Controller
@Transactional
@Secured("ROLE_ADMIN")
@RequestMapping("/schedule-events")
public class ScheduleController {

    private final ScheduleEventTypeService eventTypeService;
    private final ScheduleService scheduleService;
    private final UserService userService;
    @Autowired private ConversionService conversionService;
    @Autowired private RoleService roleService;

    @Autowired
    public ScheduleController(
            final ScheduleEventTypeService eventTypeService,
            final ScheduleService scheduleService,
            final UserService userService) {
        this.eventTypeService = eventTypeService;
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @GetMapping("/type/")
    public ModelAndView displayScheduleEventTypes(final Model model) {

        return prepareModelAndView(0L, model);
    }

    private ModelAndView prepareModelAndView(Long typeId, final Model model) {
        if (typeId == null) {
            typeId = 0L;
        }
        ModelAndView modelAndView = new ModelAndView("event/event_type");
        model.addAttribute("ownersRoles", roleService.getAllRoles().stream()
                .filter(role -> !role.getName().equals("ROLE_GUEST"))
                .collect(Collectors.toList()));
        model.addAttribute("participantsRoles", roleService.getAllRoles().stream()
                .filter(role -> !role.getName().equals("ROLE_ADMIN"))
                .collect(Collectors.toList()));
        model.addAttribute("eventTypes",
                eventTypeService.getAllEventTypesSorted().stream()
                        .map(eventType -> conversionService.convert(eventType, ScheduleEventTypeDTO.class))
                        .collect(Collectors.toList()));
        model.addAttribute("eventType",
                conversionService.convert(
                        eventTypeService.getEventTypeById(typeId).orElse(new ScheduleEventType()),
                        ScheduleEventTypeDTO.class));
        modelAndView.addObject(model);

        return modelAndView;
    }

    @PostMapping("/type/add")
    public ModelAndView processEventTypeListActionAdd(@ModelAttribute("new_name") String name, Model model) {
        try {
            if (name.isEmpty()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE);
            }
        } catch (BindingResultException e) {
            ModelAndView modelAndView = prepareModelAndView(0L, model);
            model.addAttribute("error", e.getMessage());
            modelAndView.addObject(model);

            return modelAndView;
        }

        ScheduleEventType newEventType = new ScheduleEventType(name, 1);
        newEventType.addOwner(roleService.getRoleByName("ROLE_ADMIN"));
        eventTypeService.saveEventType(newEventType);

        return new ModelAndView("redirect:/schedule-events/type/");
    }

    @PostMapping("/type/")
    public String processFormCreateScheduleEventType(final Principal principal,
                                                     @Valid final ScheduleEventTypeDTO req,
                                                     final Model model,
                                                     final Errors bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventType", req);
            return "event/event_type";
        }

        return "redirect:/schedule-events/type/";
    }
}
