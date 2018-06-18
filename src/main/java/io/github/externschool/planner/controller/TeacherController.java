package io.github.externschool.planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TeacherController {

    //TeacherServiceImpl teacherService;

    @GetMapping({"/teacher/profile"})
    public String teacher() {
        return "teacher/teacher_profile";
    }

    @PostMapping({"/teacher/profile"})
    public String save() {
        return "/";
    }
}