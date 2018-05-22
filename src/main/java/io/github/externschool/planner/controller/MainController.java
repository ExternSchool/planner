package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class MainController {

    private UserServiceImpl userService;


    @Autowired
    public MainController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping({"", "/", "/index", "/home", "/greeting"})
    public String greeting() {
        return "greeting";
    }


    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        userService.save(user);
        return "redirect:/login";
    }



}