package io.github.externschool.planner.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//TODO merge with #17
@Controller
public class HomeController {

    @GetMapping("/login")
    public String login() {
        return "/login";
    }
}

