package io.github.externschool.planner.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"", "/", "/index", "/home"})
    public String home() {
        return "/home";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/user")
    public String user() {
        return "/user";
    }

    @GetMapping("/error")
    public String error() {
        return "/error";
    }
}
