package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String newUser(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }

        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView saveOrUpdate(UserDTO userDTO, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        redirectAttributes.addFlashAttribute("user", userDTO);
        try {
            userService.createNewUser(userDTO);
            modelAndView.setViewName("redirect:/login");
        } catch (EmailExistsException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/signup");
        }

        return modelAndView;
    }
}
