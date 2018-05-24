package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.UserServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @Autowired
    UserServiceImpl userService;


    //TODO remove this method when any page that requires authorisation is available, please
    //TODO "success" form is now accessible with mapping only in test purpose
    @RequestMapping("/success")
    public String homeUser(Model model) {
        model.addAttribute("user", new UserDTO());
        return "success";
    }

    //TODO Add service to prevent sign up for logged in users
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String newUser(Model model) {
        model.addAttribute("user", new UserDTO());
        return "signup";
    }

    //TODO Browser-side SignUp form validation used
    //TODO Implement DTO validation to avoid fake data submitted in POST requests
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String saveOrUpdate(UserDTO userDTO, Model model, User user) {
//        model.addAttribute("user", userDTO);
        user = userDTO.constructUser();

        userService.createNewUser(user);

        return "success";
    }
}
