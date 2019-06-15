package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventTypeDTO;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.exceptions.EventTypeCanNotBeDeletedException;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/event")
public class ScheduleController {
    private final ScheduleEventTypeService eventTypeService;
    private final ScheduleService scheduleService;
    private final ConversionService conversionService;
    private final RoleService roleService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @Autowired
    public ScheduleController(
            final ScheduleEventTypeService eventTypeService,
            final ScheduleService scheduleService,
            final ConversionService conversionService,
            final RoleService roleService) {
        this.eventTypeService = eventTypeService;
        this.scheduleService = scheduleService;
        this.conversionService = conversionService;
        this.roleService = roleService;
    }

    @GetMapping("/type/")
    public ModelAndView displayEventTypesList(final ModelMap model) {

        return prepareModelAndView(null, model);
    }

    @PostMapping("/type/add")
    public ModelAndView processEventTypeAddForm(@ModelAttribute("new_name") String name, ModelMap model) {
        try {
            if (name.isEmpty()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE);
            }
        } catch (BindingResultException e) {
            ModelAndView modelAndView = prepareModelAndView(null, model);
            model.addAttribute("error", e.getMessage());
            modelAndView.addObject(model);

            return modelAndView;
        }

        ScheduleEventType newEventType = new ScheduleEventType(name, 1);
        newEventType.setDurationInMinutes(45);
        newEventType.addOwner(roleService.getRoleByName("ROLE_TEACHER"));
        newEventType.addParticipant(roleService.getRoleByName("ROLE_STUDENT"));
        eventTypeService.saveEventType(newEventType);

        return new ModelAndView("redirect:/event/type/");
    }

    @PostMapping(value = "/type/", params = "action=save")
    public ModelAndView processEventTypeFormSave(@ModelAttribute("eventType") @Valid ScheduleEventTypeDTO req,
                                                     final BindingResult bindingResult,
                                                     final ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventType", req);
            return prepareModelAndView(req.getId(), model);
        }

        eventTypeService.saveEventType(conversionService.convert(req, ScheduleEventType.class));

        return new ModelAndView("redirect:/event/type/", model);
    }

    @GetMapping("/type/{id}")
    public ModelAndView displayEventTypeForm(@PathVariable("id") Long id, ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("redirect:/event/type/");
        Optional<ScheduleEventType> eventType = eventTypeService.getEventTypeById(id);
        if(eventType.isPresent()) {
            ScheduleEventType type = eventType.get();
            modelAndView = prepareModelAndView(type.getId(), model);
        }

        return modelAndView;
    }

    @GetMapping("/type/{id}/modal")
    public ModelAndView displayDeleteEventTypeModal(@PathVariable("id") Long id, ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("event/event_type :: deleteEventType");
        Optional<ScheduleEventType> eventType = eventTypeService.getEventTypeById(id);
        if(eventType.isPresent()) {
            ScheduleEventType type = eventType.get();
            boolean impossibleToDeleteType = scheduleService.getEventsByType(type).stream().findFirst().isPresent();
            if (impossibleToDeleteType) {
                modelAndView.addObject(prepareModelAndView(null, model).getModel());
            } else {
                modelAndView.addObject(prepareModelAndView(type.getId(), model).getModel());
            }
        }

        return modelAndView;
    }

    @PostMapping("/type/{id}/delete")
    public ModelAndView processDeleteEventType(@PathVariable("id") Long id, ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("redirect:/event/type/", model);
        Optional<ScheduleEventType> eventType = eventTypeService.getEventTypeById(id);
        try {
            eventType.ifPresent(eventTypeService::deleteEventType);
        } catch (EventTypeCanNotBeDeletedException e) {
            // TODO Add a message to the web template
        }

        return modelAndView;
    }

    private ModelAndView prepareModelAndView(Long typeId, final ModelMap model) {
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
        ScheduleEventTypeDTO defaultEventTypeDTO = new ScheduleEventTypeDTO(null, "", 1,
                Collections.singletonList(roleService.getRoleByName("ROLE_TEACHER")),
                Collections.singletonList(roleService.getRoleByName("ROLE_STUDENT")));
        model.addAttribute("eventType",
                eventTypeService.getEventTypeById(typeId)
                        .map(t -> conversionService.convert(t, ScheduleEventTypeDTO.class))
                        .orElse(defaultEventTypeDTO));
        modelAndView.addObject(model);

        return modelAndView;
    }
}
