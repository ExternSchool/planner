package io.github.externschool.planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"", "/", "/index", "/home", "/greeting"})
    public String greeting() {
        return "greeting";
    }

}