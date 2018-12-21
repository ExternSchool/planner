package io.github.externschool.planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    @GetMapping({"", "/", "/index", "/home", "/main"})
    public ModelAndView displayMainPage(@RequestParam(value = "search", required = false) String request) {
        return new ModelAndView("main");
    }

    @GetMapping("/login")
    public ModelAndView displayLoginForm() {
        return new ModelAndView("login");
    }
}