package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Controller
public class ScheduleController {

    private final ScheduleEventTypeService eventTypeService;
    private final ScheduleService scheduleService;
    private final UserService userService;

    @Autowired
    public ScheduleController(
            final ScheduleEventTypeService eventTypeService,
            final ScheduleService scheduleService,
            final UserService userService
    ) {
        this.eventTypeService = eventTypeService;
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @GetMapping("/schedule-events/new")
    public String displayFromCreateScheduleEvent(final Model model) {
        model.addAttribute("eventTypes", this.eventTypeService.loadEventTypes());
        model.addAttribute("newEvent", new ScheduleEventReq());
        return "scheduleEvents/formCreateScheduleEvent";
    }

    @PostMapping("/schedule-events/new")
    public String processFormCreateScheduleEvent(
            final Principal principal,
            @Valid final ScheduleEventReq req,
            final Model model,
            final Errors bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("newEvent", req);
            return "scheduleEvents/formCreateScheduleEvent";
        }

        final User user = userService.findUserByEmail(principal.getName());

        this.scheduleService.createEvent(user, req);

        return "redirect:/schedule-events";

    }
}
