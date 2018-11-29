package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.exceptions.BindingResultException;
import io.github.externschool.planner.exceptions.EmailExistsException;
import io.github.externschool.planner.exceptions.KeyNotValidException;
import io.github.externschool.planner.exceptions.RoleNotFoundException;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_FORM_INVALID_KEY_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;

@Controller
@RequestMapping("/guest")
public class GuestController {
    private final PersonService personService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final RoleService roleService;
    private final UserService userService;

    public GuestController(final PersonService personService,
                           final ConversionService conversionService,
                           final VerificationKeyService keyService,
                           final RoleService roleService,
                           final UserService userService) {
        this.personService = personService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/")
    public ModelAndView showGuestList(){
        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        List<PersonDTO> persons = personService.findAllByOrderByName().stream()
                .map(p -> p.getClass().equals(Person.class) ? conversionService.convert(p, PersonDTO.class) : null)
                .filter(Objects::nonNull)
                .filter(p -> (p.getVerificationKey() == null)
                        || (p.getVerificationKey() != null
                            && keyService.findKeyByValue(p.getVerificationKey().getValue()).getUser() != null
                            && !keyService.findKeyByValue(p.getVerificationKey().getValue()).getUser().getRoles()
                        .contains(roleAdmin)))
                .collect(Collectors.toList());

        return new ModelAndView("guest/person_list", "persons", persons);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/profile")
    public ModelAndView showFormPersonProfile(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();
        PersonDTO personDTO =  conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView showFormPersonProfileToEdit(@PathVariable("id") Long id){
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView deletePersonProfile(@PathVariable("id") Long id){
        personService.deletePerson(personService.findPersonById(id));

        return new ModelAndView("redirect:/guest/");
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processFormPersonProfileActionSave(@ModelAttribute("person") @Valid PersonDTO personDTO,
                                                           BindingResult bindingResult,
                                                           Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                if((bindingResult.getAllErrors().get(0)).getDefaultMessage().contains("verificationKey")) {
                    throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);
                }
                throw new BindingResultException(UK_FORM_VALIDATION_ERROR_MESSAGE);
            }

            Person persistedPerson = personService.findPersonById(personDTO.getId());
            VerificationKey persistedKey = persistedPerson.getVerificationKey();
            User user = persistedKey.getUser();
            VerificationKey newKey = personDTO.getVerificationKey();
            if (newKey != null && newKey != persistedKey) {
                if (newKey.getUser() != null) {
                    throw new KeyNotValidException(UK_FORM_INVALID_KEY_MESSAGE);
                }
                Person newPerson = newKey.getPerson();
                if (user != null && newPerson != null && newPerson.getClass() != Person.class) {
                    user.removeVerificationKey();
                    personService.deletePerson(persistedPerson);
                    user.addVerificationKey(newKey);
                    userService.assignNewRolesByKey(user, newKey);
                    userService.save(user);
                    if (userService.findUserByEmail(principal.getName())
                            .getRoles()
                            .contains(roleService.getRoleByName("ROLE_ADMIN"))) {
                        return new ModelAndView("redirect:/guest/");
                    }

                    return new ModelAndView("redirect:/logout");
                }
            }
            personDTO.setVerificationKey(persistedKey);
            personService.saveOrUpdatePerson(conversionService.convert(personDTO, Person.class));
        } catch (BindingResultException | EmailExistsException | KeyNotValidException | RoleNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("guest/person_profile");
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("person", personDTO);
            modelAndView.addObject("isNew", false);

            return modelAndView;
        }

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView processFormPersonProfileActionCancel(Principal principal) {

        return redirectByRole(userService.findUserByEmail(principal.getName()));
    }

    private ModelAndView redirectByRole(User user) {
        if (userService.findUserByEmail(user.getEmail())
                .getRoles()
                .contains(roleService.getRoleByName("ROLE_ADMIN"))) {

            return new ModelAndView("redirect:/guest/");
        }

        return new ModelAndView("redirect:/");
    }

    private ModelAndView showPersonProfileForm(PersonDTO personDTO, Boolean isNew){
        ModelAndView modelAndView = new ModelAndView("guest/person_profile");
        modelAndView.addObject("person", personDTO);
        modelAndView.addObject("isNew",isNew);

        return modelAndView;
    }
}
