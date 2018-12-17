package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.KeyNotValidException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;

@Controller
@Transactional
public class UserController {
    private final UserService userService;
    private final VerificationKeyService keyService;
    private final ConversionService conversionService;

    @Autowired
    public UserController(final UserService userService,
                          final VerificationKeyService keyService,
                          final ConversionService conversionService) {
        this.userService = userService;
        this.keyService = keyService;
        this.conversionService = conversionService;
    }

    @GetMapping(value = "/signup")
    public String displaySignUpForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }

        return "signup";
    }

    @PostMapping(value = "/signup")
    public ModelAndView processSignUpForm(@Valid UserDTO userDTO,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        User user;
        try {
            if (bindingResult.hasErrors()) {
                String result = (bindingResult.getFieldErrors().get(0)).getDefaultMessage();
                if(result != null && result.contains("verificationKey")) {
                    throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);
                }
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
            user = userService.createNewUser(userDTO);
            if (userDTO.getVerificationKey() == null) {
                userService.createAndAddNewKeyAndPerson(user);
                userService.save(user);
            } else {
                VerificationKey key = keyService.findKeyByValue(userDTO.getVerificationKey().getValue());
                if (key == null || key.getUser() != null) {
                    userDTO.setVerificationKey(null);
                    throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);
                }
                if (key.getPerson() != null && key.getPerson().getClass() != Person.class) {
                    user.addVerificationKey(key);
                    userService.save(user);
                    userService.assignNewRolesByKey(user, key);
                }
            }
        } catch (BindingResultException | EmailExistsException | KeyNotValidException | RoleNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/signup");
            modelAndView.addObject("error", e.getMessage());
            redirectAttributes.addFlashAttribute("user", userDTO);

            return modelAndView;
        }
        redirectAttributes.addFlashAttribute("email", userDTO.getEmail());

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/init")
    public ModelAndView initiateUserProfileLoading(final Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getUserByEmail(principal.getName());
        if (user.getVerificationKey() == null) {
            userService.createAndAddNewKeyAndPerson(user);
            userService.save(user);
        }
        if (user.getVerificationKey().getPerson().getPhoneNumber() == null) {
            modelAndView.setViewName("/guest/person_profile");
            modelAndView.addObject("person",
                    conversionService.convert(user.getVerificationKey().getPerson(), PersonDTO.class));
            modelAndView.addObject("isNew", true);
        } else {
            modelAndView.setViewName("redirect:/");
        }

        return modelAndView;
    }
}
