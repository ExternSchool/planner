package io.github.externschool.planner.web;

import io.github.externschool.planner.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    //TODO check returned view
    @RequestMapping("/user/success")
    public String homeUser(Model model){
        model.addAttribute("user", new UserDTO());
        return "/user/success";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String newUser(Model model){
        model.addAttribute("user", new UserDTO());
        return "/user/signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String saveOrUpdate(UserDTO userDTO, Model model) {
        model.addAttribute("user", userDTO);
        return "/user/success";
    }
}
