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
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserService userService;
    private final VerificationKeyService keyService;
    private final PersonService personService;
    private final ConversionService conversionService;

    public UserController(final UserService userService,
                          final VerificationKeyService keyService,
                          final PersonService personService,
                          final ConversionService conversionService) {
        this.userService = userService;
        this.keyService = keyService;
        this.personService = personService;
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
        User user = new User();
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(
                        bindingResult.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", ")));
            }
            user = userService.createNewUser(userDTO);
            if (userDTO.getVerificationKeyValue() != null && !userDTO.getVerificationKeyValue().isEmpty()) {
                VerificationKey key = keyService.findKeyByValue(userDTO.getVerificationKeyValue());
                if (key == null) {
                    throw new KeyNotValidException("Entered key is not valid");
                }
                Person person = key.getPerson();
                if (person != null && person.getClass() != Person.class) {
                    userService.saveOrUpdate(user);
                    user.addVerificationKey(key);
                    userService.assignNewRolesByKey(user, key);
                }
            }
        } catch (BindingResultException | EmailExistsException | KeyNotValidException | RoleNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/signup");
            modelAndView.addObject("error", e.getMessage());
            redirectAttributes.addFlashAttribute("user", userDTO);

            return modelAndView;
        }
        userService.saveOrUpdate(user);
        redirectAttributes.addFlashAttribute("email", userDTO.getEmail());

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/init")
    public ModelAndView setUserProfile(final Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User currentUser = userService.findUserByEmail(principal.getName());
        if (currentUser.getVerificationKey() != null && currentUser.getVerificationKey().getPerson() != null) {
            modelAndView.setViewName("redirect:/");
        } else {
            VerificationKey key = keyService.saveOrUpdateKey(new VerificationKey());
            Person person = new Person();
            person.addVerificationKey(key);
            personService.saveOrUpdatePerson(person);
            currentUser.addVerificationKey(key);
            userService.saveOrUpdate(currentUser);

            modelAndView.setViewName("/guest/person_profile");
            modelAndView.addObject("person", conversionService.convert(person, PersonDTO.class));
            modelAndView.addObject("isNew", true);
        }

        return modelAndView;
    }
}
