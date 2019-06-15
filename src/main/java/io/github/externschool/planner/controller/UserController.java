package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_USER_ACCOUNT_CANNOT_BE_CONFIRMED;

@Controller
public class UserController {
    private final UserService userService;
    private final VerificationKeyService keyService;
    private final ConversionService conversionService;
    private final EmailService emailService;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Autowired
    public UserController(final UserService userService,
                          final VerificationKeyService keyService,
                          final ConversionService conversionService,
                          final EmailService emailService) {
        this.userService = userService;
        this.keyService = keyService;
        this.conversionService = conversionService;
        this.emailService = emailService;
    }

    @GetMapping(value = "/signup")
    public ModelAndView displaySignUpForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }

        return new ModelAndView("signup", model.asMap());
    }

    @PostMapping(value = "/signup")
    public ModelAndView processSignUpForm(@Valid UserDTO userDTO,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        User user;
        try {
            if (bindingResult.hasErrors()) {
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }
            user = userService.createNewUser(userDTO);
            userService.createNewKeyWithNewPersonAndAddToUser(user);
            userService.save(user);
            executor.execute(() -> {
                if(emailService.emailIsValid(user.getEmail())) {
                    emailService.sendVerificationMail(user);
                }
            });
        } catch (BindingResultException | EmailExistsException | RoleNotFoundException e) {
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
            userService.createNewKeyWithNewPersonAndAddToUser(user);
            userService.save(user);
        }
        if (user.getVerificationKey().getPerson().getPhoneNumber() == null) {
            modelAndView.setViewName("redirect:/guest/profile");
            modelAndView.addObject("person",
                    conversionService.convert(user.getVerificationKey().getPerson(), PersonDTO.class));
            modelAndView.addObject("isNew", true);
        } else {
            modelAndView.setViewName("redirect:/");
        }

        return modelAndView;
    }

    @GetMapping("/confirm-registration")
    public ModelAndView confirmEmail(@RequestParam(value = "token") String request) {
        VerificationKey key = keyService.findKeyByValue(request);
        if (key != null && key.getUser() != null && !key.getUser().isEnabled()) {
            User user = key.getUser();
            user.setEnabled(true);
            userService.save(user);

            return new ModelAndView("redirect:/login");
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/signup");
        modelAndView.addObject("error", UK_USER_ACCOUNT_CANNOT_BE_CONFIRMED);

        return modelAndView;
    }
}
