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
import io.github.externschool.planner.service.PersonServiceImpl;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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

@Controller
@RequestMapping("/guest")
public class GuestController {
    private final PersonServiceImpl personService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final RoleService roleService;
    private final UserService userService;

    public GuestController(final PersonServiceImpl personService,
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
    public ModelAndView displayGuestList(){
        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        List<PersonDTO> persons = personService.findAllByOrderByName().stream()
                .map(p -> p.getClass().equals(Person.class) ? conversionService.convert(p, PersonDTO.class) : null)
                .filter(Objects::nonNull)
                .filter(p -> (!p.getVerificationKeyValue().isEmpty() &&
                        !keyService.findKeyByValue(p.getVerificationKeyValue()).getUser().getRoles().contains(roleAdmin)))
                .collect(Collectors.toList());

        return new ModelAndView("guest/person_list", "persons", persons);
    }

    @Secured("ROLE_GUEST")
    @GetMapping("/profile")
    public ModelAndView displayFormPersonProfile(final Principal principal) {
        final User user = userService.findUserByEmail(principal.getName());
        Long id = user.getVerificationKey().getPerson().getId();
        PersonDTO personDTO =  conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public ModelAndView displayEditFormPersonProfile(@PathVariable("id") Long id){
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return showPersonProfileForm(personDTO, false);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ModelAndView displayAddFormPersonProfile(){
        return showPersonProfileForm(new PersonDTO(), true);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public ModelAndView deletePersonProfile(@PathVariable("id") Long id){
        personService.deletePerson(personService.findPersonById(id));

        return new ModelAndView("redirect:/guest/");
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView processSaveFormPersonProfile(@Valid @ModelAttribute("person") PersonDTO personDTO,
                                                     BindingResult bindingResult,
                                                     Principal principal) {
            try {
                if (bindingResult.hasErrors()) {
                    throw new BindingResultException(
                            bindingResult.getAllErrors().stream()
                                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .collect(Collectors.joining(", ")));
                }
                if (!personDTO.getVerificationKeyValue().isEmpty()) {
                    VerificationKey key = keyService.findKeyByValue(personDTO.getVerificationKeyValue());
                    if (key == null) {
                        throw new KeyNotValidException("Entered key is not valid");
                    }
                    User user = key.getUser();
                    Person person = key.getPerson();
                    if (person != null && person.getClass() != Person.class) {
                        user.addVerificationKey(key);
                        userService.assignNewRolesByKey(user, key);
                        userService.saveOrUpdate(user);
                    }
                }
            } catch (BindingResultException | EmailExistsException | KeyNotValidException | RoleNotFoundException e) {
                ModelAndView modelAndView = new ModelAndView("guest/person_profile");
                modelAndView.addObject("error", e.getMessage());
                modelAndView.addObject("person", personDTO);
                modelAndView.addObject("isNew", false);

                return modelAndView;
            }

        return redirectByRole(principal);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GUEST"})
    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView processCancelFormPersonProfile(Principal principal) {

        return redirectByRole(principal);
    }

    private PersonDTO setNewKey(PersonDTO personDTO) {
        VerificationKey newKey = keyService.findKeyByValue(personDTO.getVerificationKeyValue());
        if (newKey != null) {
            VerificationKey foundKey = keyService.findKeyByValue(newKey.getValue());
            if (foundKey != null &&
                    foundKey.getUser() == null &&
                    !foundKey.getPerson().getClass().equals(Person.class)) {
                //TODO bind this user to a new person
                //TODO remove this print out
                System.out.println("\n\nA New Profile for This User Found!!!\n\n");
            } else {
                //New key doesn't match, fix it with a database stored for this Id
                personDTO.setVerificationKeyValue(
                        personService.findPersonById(personDTO.getId()).getVerificationKey().getValue());
            }
        }

        return personDTO;
    }

    private ModelAndView redirectByRole(Principal principal) {
        if (userService.findUserByEmail(principal.getName()).getRoles()
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
